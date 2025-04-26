package com.spoteditor.backend.integration.connection;

import com.spoteditor.backend.config.RedisTestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@DataRedisTest
@Import(RedisTestConfiguration.class)
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("Redis 연결 확인")
    void 레디스_연결_테스트(){
        String key = "testKey";
        String value = "testValue";

        redisTemplate.opsForValue().set(key, value);
        String result = redisTemplate.opsForValue().get(key);

        assertEquals(value, result);
    }
}
