package com.spoteditor.backend.modules.notification.config;

import com.spoteditor.backend.modules.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void followPublish(NotificationDto dto) {
		rabbitTemplate.convertAndSend(
				RabbitMqConfiguration.FOLLOW_EXCHANGE_NAME,
				RabbitMqConfiguration.FOLLOW_ROUTING_KEY,
				dto);
		log.info("[#] Published Follow Notification: " + dto);
	}
}
