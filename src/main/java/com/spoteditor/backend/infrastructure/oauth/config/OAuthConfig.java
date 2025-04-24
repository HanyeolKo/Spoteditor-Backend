package com.spoteditor.backend.infrastructure.oauth.config;

import com.spoteditor.backend.infrastructure.oauth.repository.RedisOAuth2AuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class OAuthConfig {

    // 순환 참조로 인해 security config에서 분리
    @Bean
    public RedisOAuth2AuthorizationRequestRepository redisAuthorizationRequestRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationRequestRepository(redisTemplate);
    }

}
