package com.spoteditor.backend.modules.notification.event;

import com.spoteditor.backend.modules.notification.dto.NotificationDto;
import com.spoteditor.backend.modules.notification.entity.Notification;
import com.spoteditor.backend.modules.notification.entity.NotificationType;
import com.spoteditor.backend.modules.user.entity.User;

public record NotificationEventDto(
	NotificationDto notificationDto
) {

	public static NotificationEventDto from(User fromUser, User toUser, NotificationType type, String message) {
		return new NotificationEventDto(NotificationDto.from(fromUser, toUser, type, message));
	}

	public Notification toEntity(User fromUser, User toUser, NotificationType type, String message) {
		return Notification.builder()
                .message(message)
                .type(type)
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
	}
}