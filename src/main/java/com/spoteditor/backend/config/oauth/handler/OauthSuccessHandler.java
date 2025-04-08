package com.spoteditor.backend.config.oauth.handler;

import com.spoteditor.backend.config.jwt.JwtConstants;
import com.spoteditor.backend.config.jwt.JwtUtils;
import com.spoteditor.backend.config.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OauthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

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

        // redirect
        response.sendRedirect(successRedirectUrl);
    }
}
