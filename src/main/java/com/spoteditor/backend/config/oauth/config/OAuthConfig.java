package com.spoteditor.backend.config.oauth.config;

import com.spoteditor.backend.config.oauth.service.RedisOAuth2AuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class OAuthConfig {

    // 순환 참조로 인해 security config에서 분리
    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> redisAuthorizationRequestRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationRequestRepository(redisTemplate);
    }

}
