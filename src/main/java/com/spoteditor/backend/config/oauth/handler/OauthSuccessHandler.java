package com.spoteditor.backend.config.oauth.handler;

import com.spoteditor.backend.config.jwt.constants.JwtConstants;
import com.spoteditor.backend.config.jwt.utils.JwtUtils;
import com.spoteditor.backend.config.oauth.constants.OAuthConstants;
import com.spoteditor.backend.config.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Value("${app.oauth.success-redirect-url}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> attributesMap = oauthUser.getAttributes();
        Long id = (Long) attributesMap.get("id");

        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String accessToken = jwtUtils.createAccessToken(id, role);
        String refreshToken = jwtUtils.createRefreshToken(id, role);

        cookieUtils.setAccessTokenCookie(response, JwtConstants.ACCESS_TOKEN, accessToken);
        cookieUtils.setRefreshTokenCookie(response, JwtConstants.REFRESH_TOKEN, refreshToken);

        // 이미 security 로직상 삭제 되었음
        //OAuth2AuthorizationRequest authorizationRequest = authorizationRequestRepository.removeAuthorizationRequest(request, response);

        Cookie redirectCookie = WebUtils.getCookie(request, REDIRECT_COOKIE);
        String redirectUrlTemp;

        if (redirectCookie != null) {
            redirectUrlTemp = redirectCookie.getValue();
            //log.info("쿠키에서 가져온 redirect url: {}", redirectUrlTemp);

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
                .filter(allowUrl -> !redirectUrlTemp.equals("") && redirectUrlTemp.startsWith(allowUrl) )
                .findFirst()
                .orElse(successRedirectUrl);

        log.info("{} 로 리다이렉트 됨", redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
