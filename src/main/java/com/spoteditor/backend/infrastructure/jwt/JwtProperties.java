package com.spoteditor.backend.infrastructure.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Data
@Component
@ConfigurationProperties("jwt.token")
public class JwtProperties {
    private String secretKey;
    private Long accessTokenExp;
    private Long refreshTokenExp;

    private SecretKey signingKey;

    @PostConstruct
    private void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }
}
