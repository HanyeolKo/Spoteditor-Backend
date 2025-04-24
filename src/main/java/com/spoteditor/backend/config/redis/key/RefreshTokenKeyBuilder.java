package com.spoteditor.backend.config.redis.key;

import static com.spoteditor.backend.global.constants.JwtConstants.REFRESH_REDIS_KEY_PREFIX;

public class RefreshTokenKeyBuilder {

    public static String build(Long userId) {
        return build(userId, PlatformType.WEB);
    }

    public static String build(Long userId, PlatformType platformType) {
        return String.format("%s:%s:%s", REFRESH_REDIS_KEY_PREFIX, userId, platformType.toKey());
    }

    public static enum PlatformType{
        WEB,
        MOBILE,
        TABLET;

        public String toKey(){
            return this.name().toLowerCase();
        }
    }

}
