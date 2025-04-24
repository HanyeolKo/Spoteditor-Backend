package com.spoteditor.backend.config.redis.key;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static com.spoteditor.backend.global.constants.OAuthConstants.REQUEST_REDIS_TAG;

public class OAuth2RequestKeyBuilder {

    public static String build(OAuth2AuthorizationRequest authorizationRequest) {
        return REQUEST_REDIS_TAG + authorizationRequest.getState();
    }

}
