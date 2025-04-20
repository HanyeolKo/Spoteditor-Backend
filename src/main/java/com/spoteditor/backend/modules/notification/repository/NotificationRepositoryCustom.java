package com.spoteditor.backend.modules.notification.repository;

import com.spoteditor.backend.modules.notification.controller.dto.NotificationListDto;
import com.spoteditor.backend.modules.notification.entity.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {

	List<NotificationListDto> notificationList(Long userId);
	List<Notification> findAllByUserIdAndUnread(Long userId);
	List<Notification> findAllByUserIdAndRead(Long userId);
	void updateNotificationRead(Long userId);
}
