package com.spoteditor.backend.infrastructure.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

	private Cluster cluster;

	@Getter
	@Setter
	public static class Cluster {
		private List<String> nodes;
		private int maxRedirects;
	}
}
