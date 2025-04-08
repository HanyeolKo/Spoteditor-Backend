package com.spoteditor.backend.config.oauth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Base64;

/**
 *      로드밸런서를 사용하면서 OAuth2.0 인증과정에서 요청과 리다이렉트를 각각 다른 서버에서 처리하는 이슈 발생
 *      Redis에 인증 요청 정보를 저장하여 모든 분산 서버에서 상태 정보를 공유하도록 함
 */
public class RedisOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository {

    private static final String COOKIE_NAME = "OAUTH2_AUTH_REQUEST";        // Redis 키값을 저장할 쿠키 이름
    private static final Duration EXPIRATION = Duration.ofMinutes(3);       // 인증 상태의 유효기간 (최대 3분)

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisOAuth2AuthorizationRequestRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 쿠키에 들어있는 Redis 키값을 이용해 OAuth2 인증 요청 상태를 로드
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        if (cookie != null && StringUtils.hasText(cookie.getValue())) {
            String key = decodeKey(cookie.getValue());
            return (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(key);
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

        String key = generateKey(authorizationRequest);

        // redis에 request정보 저장
        redisTemplate.opsForValue().set(key, authorizationRequest, EXPIRATION);

        // redis키 쿠키에 저장
        Cookie cookie = new Cookie(COOKIE_NAME, encodeKey(key));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) EXPIRATION.getSeconds());
        response.addCookie(cookie);
    }

    /**
     * 인증 요청 완료 후 Redis와 쿠키에서 상태 정보를 제거함
     * fail / success 상관없이 호출됨
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {

        // 저장된 키값 제거
        OAuth2AuthorizationRequest requestObj = loadAuthorizationRequest(request);
        if (requestObj != null) {
            String key = generateKey(requestObj);
            redisTemplate.delete(key);
        }

        // 쿠키 강제 만료
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return requestObj;
    }

    /**
     * OAuth2AuthorizationRequest의 state 값을 기반으로 Redis 키 생성
     */
    private String generateKey(OAuth2AuthorizationRequest request) {
        return "oauth2:auth-request:" + request.getState();
    }

    private String encodeKey(String key) {
        return Base64.getUrlEncoder().encodeToString(key.getBytes());
    }

    private String decodeKey(String encodedKey) {
        return new String(Base64.getUrlDecoder().decode(encodedKey));
    }
}
