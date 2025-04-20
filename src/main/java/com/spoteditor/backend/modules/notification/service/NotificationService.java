package com.spoteditor.backend.modules.notification.service;

import com.spoteditor.backend.global.exception.NotificationException;
import com.spoteditor.backend.global.response.ErrorCode;
import com.spoteditor.backend.modules.notification.config.NotificationPublisher;
import com.spoteditor.backend.modules.notification.dto.NotificationDto;
import com.spoteditor.backend.modules.notification.entity.Notification;
import com.spoteditor.backend.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationPublisher notificationPublisher;

	/**
	 *
	 * @param dto
	 */
	@Transactional
	public void send(NotificationDto dto) {

		switch (dto.type()) {
			case FOLLOW -> notificationPublisher.followPublish(dto);	// Publisher
		}
	}

	/**
	 *
	 * @param userId
	 */
	@Transactional
	public void readAll(Long userId) {
		notificationRepository.updateNotificationRead(userId);
	}

	/**
	 *
	 * @param notificationId
	 */
	@Transactional
	public void read(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new NotificationException(ErrorCode.NOT_FOUND_NOTIFICATION));
		notification.read();
	}
}
