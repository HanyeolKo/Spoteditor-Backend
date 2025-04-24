package com.spoteditor.backend.modules.user.service;

import com.spoteditor.backend.config.jwt.repository.RefreshTokenRepository;
import com.spoteditor.backend.config.redis.key.RefreshTokenKeyBuilder;
import com.spoteditor.backend.global.utils.CookieUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static com.spoteditor.backend.global.response.ErrorCode.REFRESH_TOKEN_NOT_IN_COOKIE;
import static com.spoteditor.backend.global.response.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTokenService {

    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws TokenException {
        String refreshToken = cookieUtil.getRefreshToken(request);

        if(refreshToken == null) {
            throw new TokenException(REFRESH_TOKEN_NOT_IN_COOKIE);
        }

        try {
            // RefreshToken 검증
            UsernamePasswordAuthenticationToken authentication = jwtUtils.setAuthentication(refreshToken);

            Long userId = jwtUtils.extractUserIdFromToken(refreshToken);

            // redis 토큰값 비교조회
            if(refreshTokenRepository.isEquals(RefreshTokenKeyBuilder.build(userId), refreshToken)) {
                // 검증 성공 -> accessToken 발급

                String role = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                String accessToken = jwtUtils.createAccessToken(userId, role);
                cookieUtil.setAccessTokenCookie(response, JwtConstants.ACCESS_TOKEN, accessToken);

                //검증 완료시 리프레시 토큰 유효기간 연장 (보류)
                //refreshTokenRepository.updateExpire(RefreshTokenKeyBuilder.build(userId), Duration.ofDays(7));

            }else{
                throw new TokenException(INVALID_REFRESH_TOKEN);
            }
        } catch (ExpiredJwtException | IllegalArgumentException | MalformedJwtException | SignatureException e) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }
    }

    /**
     * refresh 토큰을 통한 access 토큰 재발행
     * @return 토큰 검증에 실패한경우 null
     */
    public String reissueAccessToken(String refreshToken){
        try {
            // 1. 인증 객체 생성 및 jwt 토큰 유효성 검증
            UsernamePasswordAuthenticationToken authentication = jwtUtils.setAuthentication(refreshToken);
            Long userId = jwtUtils.extractUserIdFromToken(refreshToken);

            // 2. Redis에서 해당 Refresh 토큰의 유효성 검증
            if (invalidRefreshToken(refreshToken, userId)) {
                String role = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                String accessToken = jwtUtils.createAccessToken(userId, role);
                return accessToken;
            }
            return null;
        }catch (ExpiredJwtException | IllegalArgumentException | MalformedJwtException | SignatureException e) {
            return null;
        }
    }

    public boolean invalidRefreshToken(String refreshToken, Long userId) {
        if(refreshToken == null) {
            throw new TokenException(REFRESH_TOKEN_NOT_IN_COOKIE);
        }

        //redis에 저장된 refresh 토큰정보와 비교 조회
        if(refreshTokenRepository.isEquals(RefreshTokenKeyBuilder.build(userId), refreshToken)) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 전달된 Refresh Token을 기반으로 Redis에 저장된 토큰 정보를 삭제
     */
    public void removeRefreshTokenOnRedis(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        Long userId = null;

        try {
            userId = jwtUtils.extractUserIdFromToken(refreshToken); // 검증 실패 가능
        } catch (ExpiredJwtException | IllegalArgumentException | MalformedJwtException | SignatureException e) {
            log.warn("RefreshToken 유효성 검증 실패: {}", e.getMessage());
        } finally {     // 유효하지 않은 jwt 토큰정보라도 Redis에서는 삭제 되어야 한다.
            try {
                if (userId != null) {
                    refreshTokenRepository.deleteToken(RefreshTokenKeyBuilder.build(userId));
                }
            } catch (Exception e) {
                log.error("Redis에서 Refresh Token 삭제 실패", e);
            }
        }
    }
}
