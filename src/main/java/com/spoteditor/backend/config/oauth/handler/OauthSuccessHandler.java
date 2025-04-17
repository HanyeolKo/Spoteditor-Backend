package com.spoteditor.backend.config.oauth.handler;

import com.spoteditor.backend.config.jwt.constants.JwtConstants;
import com.spoteditor.backend.config.jwt.utils.JwtUtils;
import com.spoteditor.backend.config.oauth.constants.OAuthConstants;
import com.spoteditor.backend.config.oauth.provider.GoogleUserInfo;
import com.spoteditor.backend.config.oauth.provider.KakaoUserInfo;
import com.spoteditor.backend.config.oauth.provider.OAuth2UserInfo;
import com.spoteditor.backend.config.util.CookieUtils;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.user.entity.User;
import com.spoteditor.backend.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.spoteditor.backend.config.oauth.constants.OAuthConstants.REDIRECT_COOKIE;
import static com.spoteditor.backend.global.response.ErrorCode.NOT_FOUND_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Value("${app.oauth.success-redirect-url}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauthUser.getAttributes();

        OAuth2UserInfo userInfo = createOAuth2UserInfo(registrationId, attributes);

        // SuccessHandler : 성공시 호출되는 핸들러
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String accessToken = jwtUtils.createAccessToken(user.getId(), role);
        String refreshToken = jwtUtils.createRefreshToken(user.getId(), role);

        cookieUtils.setAccessTokenCookie(response, JwtConstants.ACCESS_TOKEN, accessToken);
        cookieUtils.setRefreshTokenCookie(response, JwtConstants.REFRESH_TOKEN, refreshToken);

        Cookie redirectCookie = WebUtils.getCookie(request, REDIRECT_COOKIE);
        String redirectUrlTemp;

        if (redirectCookie != null) {
            redirectUrlTemp = redirectCookie.getValue();

            // 쿠키 삭제
            redirectCookie.setPath("/");
            redirectCookie.setHttpOnly(true);
            redirectCookie.setSecure(true);
            redirectCookie.setMaxAge(0);
            response.addCookie(redirectCookie);
        } else {
            redirectUrlTemp = "";
        }

        String redirectUrl = OAuthConstants.REDIRECT_WHITELIST.stream()
                .filter(allowUrl -> !redirectUrlTemp.equals("") && redirectUrlTemp.startsWith(allowUrl))
                .findFirst()
                .orElse(successRedirectUrl);

        log.info("{} 로 리다이렉트 됨", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private OAuth2UserInfo createOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleUserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("kakao")) {
            return new KakaoUserInfo(attributes);
        } else {
            log.warn("지원하지 않는 OAuth 제공자: {}", registrationId);
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + registrationId);
        }
    }
}