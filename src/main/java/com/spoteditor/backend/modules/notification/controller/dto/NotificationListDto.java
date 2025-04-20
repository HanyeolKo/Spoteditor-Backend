package com.spoteditor.backend.modules.notification.controller.dto;

import com.spoteditor.backend.modules.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationListDto(
	Long id,
	String imageUrl,
	String message,
	NotificationType type,
	LocalDateTime createdAt,
	boolean isRead
) {
}
