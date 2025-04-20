package com.spoteditor.backend.modules.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

	public static final String FOLLOW_EXCHANGE_NAME = "followExchange";
	public static final String FOLLOW_QUEUE = "followQueue";
	public static final String FOLLOW_ROUTING_KEY = "follow";

	public static final String DLX_EXCHANGE_NAME = "dlxExchange";
	public static final String DLQ_QUEUE = "deadLetterQueue";
	public static final String DLQ_ROUTING_KEY = "dlq";

	@Bean
	public Queue followQueue() {
		// 데이터 영속성을 위한 true 설정
		return QueueBuilder.durable(FOLLOW_QUEUE)
				.withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
				.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
				.ttl(5000)
				.build();
	}

	@Bean
	public DirectExchange followExchange() {
		// 사용자간 팔로우
		return new DirectExchange(FOLLOW_EXCHANGE_NAME);
	}

	@Bean
	public Binding followBinding(Queue followQueue, DirectExchange followExchange) {
		return BindingBuilder.bind(followQueue)
				.to(followExchange)
				.with(FOLLOW_ROUTING_KEY);
	}

	@Bean
	public Queue deadLetterQueue() {
		return QueueBuilder.durable(DLQ_QUEUE)
				.build();
	}

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange(DLX_EXCHANGE_NAME);
	}

	@Bean
	public Binding deadLetterBinding() {
		return BindingBuilder.bind(deadLetterQueue())
				.to(deadLetterExchange())
				.with(DLQ_ROUTING_KEY);
	}
}
