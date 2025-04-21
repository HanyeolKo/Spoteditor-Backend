package com.spoteditor.backend.modules.user.service;

import com.spoteditor.backend.global.utils.CookieUtils;
import com.spoteditor.backend.global.exception.TokenException;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import com.spoteditor.backend.global.constants.JwtConstants;
import com.spoteditor.backend.infrastructure.jwt.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import static com.spoteditor.backend.global.response.ErrorCode.REFRESH_TOKEN_NOT_IN_COOKIE;
import static com.spoteditor.backend.global.response.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws TokenException {
        String refreshToken = cookieUtils.getRefreshToken(request);

        if(refreshToken == null) {
            throw new TokenException(REFRESH_TOKEN_NOT_IN_COOKIE);
        }

        try {
            // RefreshToken 검증
            UsernamePasswordAuthenticationToken authentication = jwtUtils.setAuthentication(refreshToken);

            Long userId = ((UserIdDto) authentication.getPrincipal()).getId();
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            // 검증 성공 -> accessToken 발급
            String accessToken = jwtUtils.createAccessToken(userId, role);

            cookieUtils.setAccessTokenCookie(response, JwtConstants.ACCESS_TOKEN, accessToken);
        } catch (ExpiredJwtException | IllegalArgumentException | MalformedJwtException | SignatureException e) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }
    }

    public void removeTokens(HttpServletResponse response) throws Exception {
        cookieUtils.removeCookie(response, "/", JwtConstants.ACCESS_TOKEN);
        cookieUtils.removeCookie(response, "/", JwtConstants.REFRESH_TOKEN);
    }
}
