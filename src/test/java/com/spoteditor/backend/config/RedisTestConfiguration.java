package com.spoteditor.backend.config;

import com.spoteditor.backend.infrastructure.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.NatMapper;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.misc.RedisURI;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@TestConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
@Profile("test")
public class RedisTestConfiguration {

	private final RedisProperties redisProperties;

	@Bean
	public RedissonClient redissonClient() {
		final Config config = new Config();

		String[] nodes = redisProperties.getCluster().getNodes().toArray(String[]::new);

		config.useClusterServers()
				.setScanInterval(2000)
				.setCheckSlotsCoverage(false)
				.addNodeAddress(nodes)
				.setNatMapper(new NatMapper() {
					private final Map<String, String> natMapping = Map.of(
							"172.18.0.4:6379", "127.0.0.1:6379",
							"172.18.0.2:6379", "127.0.0.1:6380",
							"172.18.0.8:6379", "127.0.0.1:6381",
							"172.18.0.7:6379", "127.0.0.1:6382",
							"172.18.0.9:6379", "127.0.0.1:6383",
							"172.18.0.6:6379", "127.0.0.1:6384"
					);
					@Override
					public RedisURI map(RedisURI uri) {
						String key = uri.getHost() + ":" + uri.getPort();
						String mappedAddress = natMapping.get(key);
						if (mappedAddress != null) {
							String[] parts = mappedAddress.split(":");
							return new RedisURI(uri.getScheme() + "://" + parts[0] + ":" + parts[1]);
						}
						return uri;
					}
				});

		return Redisson.create(config);
	}

	@Bean
	public RedisConnectionFactory redissonConnectionFactory(RedissonClient redissonClient) {
		return new RedissonConnectionFactory(redissonClient);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		// JSON 직렬화 방식 사용
		// jdk 직렬화 방식보다 권장됨
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		//template.setValueSerializer(new JdkSerializationRedisSerializer());
		return template;
	}
}
