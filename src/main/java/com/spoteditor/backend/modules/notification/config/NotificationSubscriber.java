package com.spoteditor.backend.modules.notification.config;

import com.spoteditor.backend.modules.notification.dto.NotificationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber {

	public static final String CLIENT_URL = "/topic/notification/";

	private final SimpMessagingTemplate template;

	@RabbitListener(queues = RabbitMqConfiguration.FOLLOW_QUEUE)	// Subscriber
	public void subscribeFollow(@Valid NotificationDto dto) {
		log.info("[#] Received Notification: " + dto);
		template.convertAndSend(CLIENT_URL + dto.to(), dto);
	}
}
