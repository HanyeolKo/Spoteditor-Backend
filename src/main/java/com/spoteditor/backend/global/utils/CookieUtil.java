package com.spoteditor.backend.global.utils;

import com.spoteditor.backend.global.constants.JwtConstants;
import com.spoteditor.backend.infrastructure.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public void addCookie(HttpServletResponse response, String path, String name, String value, int maxAge) {
        final List<String> ALLOWED_PATHS = List.of("/", "/api", "/auth");

        // Path 값 전처리
        if (path == null || path.trim().isEmpty()) {
            path = "/";
        } else if (!path.startsWith("/")) {
            path = "/" + path;
        }

        // Path 경로 유효성 검사
        if (!ALLOWED_PATHS.contains(path)) {
            throw new IllegalArgumentException("'/', '/api', '/auth' 경로만 가능합니다." + path);
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        // 쿠키 유효기간: 한 달 (Jwt 는 별도 exp 관리)
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    public void removeCookie(HttpServletResponse response, String path, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findAny();
        }

        return Optional.empty();
    }

    public String getAccessToken(HttpServletRequest request) {
         return getCookie(request, JwtConstants.ACCESS_TOKEN)
                .map(Cookie::getValue)
                .orElse(null);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getCookie(request, JwtConstants.REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void setAccessTokenCookie(HttpServletResponse response, String name, String value) {
        int accessTokenExp = (int) (jwtProperties.getAccessTokenExp() / 1000);

        addCookie(response, "/", name, value, accessTokenExp);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String name, String value) {
        int refreshTokenExp = (int) (jwtProperties.getRefreshTokenExp() / 1000);

        addCookie(response, "/", name, value, refreshTokenExp);
    }


}
