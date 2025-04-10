package com.spoteditor.backend.config.oauth.constants;

import java.time.Duration;
import java.util.List;

public class OAuthConstants {

    // Redirect를 허용할 주소
    public static final List<String> REDIRECT_WHITELIST = List.of(
            "https://localhost:5173",
            "https://spoteditor-frontend.vercel.app",
            "https://spoteditor.duckdns.org"
    );

    // 쿠키 관련 상수
    public static final String OAUTH_REQUEST_COOKIE = "OAUTH2_AUTH_REQUEST";        // 요청 정보를 저장할 Redis 키값을 저장할 쿠키 이름
    public static final String REDIRECT_COOKIE = "REDIRECT";
    public static final Duration EXPIRATION = Duration.ofMinutes(3);       // 인증 상태의 유효기간 (최대 3분)
    
    // Redis key tag
    public static final String REQUEST_REDIS_TAG = "oauth2:auth-request:";

    /**
     * OAuth2 Request에 저장될 파라미터 명칭
     */
    public static final String OAUTH2_REQUEST_PARAM = "redirect_uri_after_login";

}
