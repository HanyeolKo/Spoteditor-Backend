package com.spoteditor.backend.modules.notification.handler;

import com.spoteditor.backend.modules.notification.event.NotificationEventDto;
import com.spoteditor.backend.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

	private final NotificationService notificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async
	public void handleNotificationEvent(NotificationEventDto eventDto) {

		log.info("Alarm Thread Name:{}", Thread.currentThread().getName());
		notificationService.send(eventDto.notificationDto());
	}
}
