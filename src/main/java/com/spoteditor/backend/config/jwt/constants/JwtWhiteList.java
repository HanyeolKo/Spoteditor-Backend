package com.spoteditor.backend.config.jwt.constants;

import java.util.List;

public class JwtWhiteList {
    public static final List<String> ALL_METHOD_WHITE_LIST = List.of(
            "/error",
            "/api/auth/**"
    );

    public static final List<String> GET_METHOD_WHITE_LIST = List.of(
            "/favicon.ico",
            "/api/health",
            "/api/docs/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/placelogs",
            "/api/search/placelogs/**"
    );

    public static final List<String> GUEST_WHITE_LIST = List.of(
            "/api/placelogs/**",
            "/api/users/**"
    );
}
