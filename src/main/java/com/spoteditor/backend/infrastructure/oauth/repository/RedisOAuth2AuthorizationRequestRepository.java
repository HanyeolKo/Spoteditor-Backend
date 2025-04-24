package com.spoteditor.backend.infrastructure.oauth.repository;

import com.spoteditor.backend.config.redis.key.OAuth2RequestKeyBuilder;
import com.spoteditor.backend.config.util.Base64Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import java.util.*;

import static com.spoteditor.backend.global.constants.OAuthConstants.*;

/**
 *      로드밸런서를 사용하면서 OAuth2.0 인증과정에서 요청과 리다이렉트를 각각 다른 서버에서 처리하는 이슈 발생
 *      Redis에 인증 요청 정보를 저장하여 모든 분산 서버에서 상태 정보를 공유하도록 함
 */
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 쿠키에 들어있는 Redis 키값을 이용해 OAuth2 인증 요청 상태를 로드
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH_REQUEST_COOKIE);
        if (cookie != null && StringUtils.hasText(cookie.getValue())) {
            String key = Base64Util.URLDECODE(cookie.getValue());
            Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
            return deserializeAuthorizationRequest(data);
        }
        return null;
    }

    /**
     * 인증 요청 정보를 Redis에 저장하고, Redis 키를 쿠키에 저장
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        String key = OAuth2RequestKeyBuilder.build(authorizationRequest);

        Map<String, String> serializedRequest = serializeAuthorizationRequest(authorizationRequest);

        // redis에 request정보 저장
        redisTemplate.opsForHash().putAll(key, serializedRequest);
        redisTemplate.expire(key, REQUEST_TEMP_EXPIRATION);

        //log.info("직렬화된 OAuth2AuthorizationRequest 정보 : {}", serializedRequest);

        /**
         *  security 내부적으로 success handler 호출전 remove 호출때문에 success handler에서 해당 파라미터값을 못읽어오는 변수 발생
         *  일회성 쿠키로 redirect 주소를 저장한뒤 success handler에서 가져오는 방식으로 변경
         */
        // cookie에 redirect uri 저장
        // success handler에서 사용후 지워주기 혹은 방치
        String redirect = Optional.ofNullable(authorizationRequest.getAdditionalParameters().get(OAUTH2_REQUEST_PARAM))
                .map(Object::toString)
                .orElse("");

        Cookie redirectCookie = new Cookie(REDIRECT_COOKIE, redirect);
        redirectCookie.setPath("/");
        redirectCookie.setHttpOnly(true);
        redirectCookie.setSecure(true);
        redirectCookie.setMaxAge(60);       // 1분
        response.addCookie(redirectCookie);

        //log.info("redirect 주소 COOKIE 저장완료 : {}::{}", REDIRECT_COOKIE, redirect);

        // redis키 쿠키에 저장
        Cookie cookie = new Cookie(OAUTH_REQUEST_COOKIE, Base64Util.URLENCODE(key));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) REQUEST_TEMP_EXPIRATION.getSeconds());
        response.addCookie(cookie);


    }

    /**
     * 인증 요청 완료 후 Redis와 쿠키에서 상태 정보를 제거함
     * fail / success 상관없이 호출됨
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {

        OAuth2AuthorizationRequest requestObj = loadAuthorizationRequest(request);

        // Redis 상태 정보 제거
        if (requestObj != null) {
            String key = OAuth2RequestKeyBuilder.build(requestObj);
            redisTemplate.delete(key);
        }

        // 인증정보 redis 주소 쿠키 강제 만료
        Cookie cookie = new Cookie(OAUTH_REQUEST_COOKIE, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return requestObj;
    }
    /**
     *  역/직렬화시 JSON 타입으로 저장하는 것을 권장
     *  Redis상에서 값 확인이 쉬움
     */

    final String ADDITIONAL_TAG = "additional:";

    /**
     * JSON 타입 직렬화
     */
    private Map<String, String> serializeAuthorizationRequest(OAuth2AuthorizationRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("authorizationUri", request.getAuthorizationUri());
        map.put("clientId", request.getClientId());
        map.put("redirectUri", request.getRedirectUri());
        map.put("state", request.getState());
        map.put("scope", String.join(",", request.getScopes()));
        map.put("authorizationRequestUri", request.getAuthorizationRequestUri());

        // registrationId는 attributes에서 가져와야 함
        Object regId = request.getAttributes().get("registration_id");
        if (regId != null) {
            map.put("registrationId", regId.toString());
        }

        // additionalParameters 저장
        request.getAdditionalParameters().forEach((k, v) -> {
            if (v != null) {
                map.put(ADDITIONAL_TAG + k, v.toString());
            }
        });

        return map;
    }

    /**
     * JSON 타입 역직렬화
     */
    private OAuth2AuthorizationRequest deserializeAuthorizationRequest(Map<Object, Object> data) {
        Set<String> scopes = new HashSet<>(List.of(((String) data.get("scope")).split(",")));

        OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri((String) data.get("authorizationUri"))
                .clientId((String) data.get("clientId"))
                .redirectUri((String) data.get("redirectUri"))
                .state((String) data.get("state"))
                .scope(scopes.toArray(new String[0]))
                .authorizationRequestUri((String) data.get("authorizationRequestUri"));

        // ✅ attributes에 registration_id 복원
        Object registrationId = data.get("registrationId");
        if (registrationId != null) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("registration_id", data.get("registrationId")); // 직접 세팅

            builder.attributes(attributes); // 전체 attributes 설정
        }

        // additionalParameters 복원
        Map<String, Object> additional = new HashMap<>();
        data.forEach((k, v) -> {
            String key = k.toString();
            if (key.startsWith(ADDITIONAL_TAG)) {
                additional.put(key.substring(ADDITIONAL_TAG.length()), v);
            }
        });

        builder.additionalParameters(additional);

        return builder.build();
    }


}
