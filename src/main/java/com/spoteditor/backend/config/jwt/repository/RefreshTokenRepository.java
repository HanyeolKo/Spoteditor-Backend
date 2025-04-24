package com.spoteditor.backend.config.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * REFRESH 토큰값 저장
     */
    public void saveToken(String key, String token, Duration ttl) {
        // 크로스 플랫폼에대한 별도의 로그인 관리 필요시 opsForHash 리팩토링의 여지 있음
        redisTemplate.opsForValue().set(key, token, ttl);
    }

    /**
     * Refresh Token 조회
     */
    public String getToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시 사용)
     */
    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 전달된 토큰이 Redis에 저장된 토큰과 같은지 확인
     */
    public boolean isEquals(String key, String requestToken) {
        String storedToken = redisTemplate.opsForValue().get(key);
        return requestToken.equals(storedToken);
    }

    /**
     * Sliding TTL 갱신 (재발급 시 사용)
     */
    public void updateExpire(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }
}
