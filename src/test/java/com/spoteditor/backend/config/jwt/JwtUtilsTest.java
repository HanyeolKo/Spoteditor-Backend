package com.spoteditor.backend.config.jwt;

import com.spoteditor.backend.config.jwt.utils.JwtUtils;
import com.spoteditor.backend.user.common.dto.UserIdDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JwtUtilsTest 테스트")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private JwtProperties jwtProperties;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        // Mock JwtProperties
        jwtProperties = mock(JwtProperties.class);

        String secret = "269de4d060ce22296b851de2044b78b7748c9c2212c2a60bdf2dc7b6d6fabc2c9e9434ee6f4175010ea740ea9c3fa5723649c7a650feffadc1ac850a1020fb32";
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);

        // Mock 설정
        when(jwtProperties.getSigningKey()).thenReturn(secretKey);
        when(jwtProperties.getAccessTokenExp()).thenReturn(600000L); // 10분

        // JwtUtils 인스턴스 생성
        jwtUtils = new JwtUtils(jwtProperties);
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    public void AccessToken_생성_테스트(){
        // given
        long id = 10L;
        String role = "ROLE_USER";

        String accessToken = jwtUtils.createAccessToken(id, role);
        UsernamePasswordAuthenticationToken authenticationToken = jwtUtils.setAuthentication(accessToken);

        // when
        UserIdDto principal = (UserIdDto) authenticationToken.getPrincipal();
        GrantedAuthority authority = authenticationToken.getAuthorities().iterator().next();

        // then
        assertThat(principal.getId()).isEqualTo(id);
        assertThat(authority.getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("토큰파싱: 만료된 토큰 ExpiredJwtException 예외")
    public void 토큰파싱_만료된토큰(){
        // given
        long id = 10L;
        String role = "ROLE_USER";

        String token = jwtUtils.createToken(id, role, -60000);

        // when & then
        assertThrows(ExpiredJwtException.class, () ->
                jwtUtils.setAuthentication(token)
        );
    }

    @Test
    @DisplayName("토큰파싱: 토큰 빈값 IllegalArgumentException 예외")
    public void 토큰파싱_비어있는_토큰_빈문자열(){
        // given
        String token = "";

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                jwtUtils.setAuthentication(token)
        );
    }

    @Test
    @DisplayName("토큰파싱: 토큰 null값 IllegalArgumentException 예외")
    public void 토큰파싱_비어있는_토큰_null(){
        // given
        String token = null;

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                jwtUtils.setAuthentication(token)
        );
    }

    @Test
    @DisplayName("토큰파싱: 잘못된 형식 (Base64인코딩 아님) MalformedJwtException 예외")
    public void 토큰파싱_잘못된형식_Base64형식_아님(){
        // given
        String invalidFormatToken = "invalid.jwt.token";

        // when & then
        assertThrows(MalformedJwtException.class, () ->
                jwtUtils.setAuthentication(invalidFormatToken)
        );
    }

    @Test
    @DisplayName("토큰파싱: 잘못된 형식 (. 2개 없음) MalformedJwtException 예외")
    public void 토큰파싱_잘못된형식_구조오류(){
        // given
        String invalidFormatToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiIxMCIsInJvbGUiOiJST0xFX1VTRVIifQ";

        // when & then
        assertThrows(MalformedJwtException.class, () ->
                jwtUtils.setAuthentication(invalidFormatToken)
        );
    }

    @Test
    @DisplayName("토큰파싱: 토큰변조 공격 SignatureException 예외")
    public void 토큰파싱_토큰변조_공격방지(){
        // given
        long id = 10L;
        String role = "ROLE_USER";
        String validToken = jwtUtils.createAccessToken(id, role);

        // when
        char lastChar = validToken.charAt(validToken.length() - 1);
        char newLastChar = (lastChar == 'x') ? 'y' : 'x';

        String manipulatedToken = validToken.substring(0, validToken.length() - 1) + newLastChar;

        // then
        assertThrows(SignatureException.class, () ->
                jwtUtils.setAuthentication(manipulatedToken)
        );
    }

    @Test
    @DisplayName("토큰파싱: alg none 공격 UnsupportedJwtException 예외")
    public void 토큰파싱_alg_none_예외(){
        // given
        String noneAlgorithmToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiIxMCIsInJvbGUiOiJST0xFX1VTRVIifQ.";

        // when & then
        assertThrows(UnsupportedJwtException.class, () ->
                jwtUtils.setAuthentication(noneAlgorithmToken)
        );
    }
}