package com.spoteditor.backend.config.jwt.utils;

import com.spoteditor.backend.config.jwt.JwtProperties;
import com.spoteditor.backend.config.jwt.constants.JwtConstants;
import com.spoteditor.backend.user.common.dto.UserIdDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String createToken(Long id, String role, long expirationTime) {
        SecretKey signingKey = jwtProperties.getSigningKey();

        return Jwts.builder()
                .signWith(signingKey, JwtConstants.ALGORITHM)
                .header()
                .add("typ", JwtConstants.TOKEN_TYPE)
                .and()
                .claim("sub", id.toString())
                .claim("role", role)
                .claim("iat", new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    public String createAccessToken(Long id, String role) {
        return createToken(id, role, jwtProperties.getAccessTokenExp());
    }

    public String createRefreshToken(Long id, String role) {
        return createToken(id, role, jwtProperties.getRefreshTokenExp());
    }

    public UsernamePasswordAuthenticationToken setAuthentication(String jwt) throws ExpiredJwtException, IllegalArgumentException, MalformedJwtException, SignatureException {
        SecretKey signingKey = jwtProperties.getSigningKey();

        Long id = Long.parseLong(parseJwtSubject(jwt, "sub", signingKey));
        String role = parseJwtSubject(jwt, "role", signingKey);

        UserIdDto userIdDto = new UserIdDto(id);
        GrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(userIdDto, null, Collections.singleton(authority));
    }

    private String parseJwtSubject(String jwt, String subject, SecretKey signingKey) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwt);

            return (String) claims.getPayload().get(subject);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException(e.getMessage(), e);
        } catch (SignatureException e) {
            throw new SignatureException(e.getMessage(), e);
        }
    }
}
