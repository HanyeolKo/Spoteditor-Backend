package com.spoteditor.backend.config;

import com.spoteditor.backend.infrastructure.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@RequiredArgsConstructor
public class RedisConfiguration {

	private final RedisProperties redisProperties;

	@Bean
	public RedissonClient redissonClient() {
		final Config config = new Config();

		String[] nodes = redisProperties.getCluster().getNodes().toArray(String[]::new);

		config.useClusterServers()
				.setScanInterval(2000)
				.addNodeAddress(nodes);

		return Redisson.create(config);
	}
}
