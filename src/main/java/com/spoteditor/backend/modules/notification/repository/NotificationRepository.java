package com.spoteditor.backend.modules.notification.repository;

import com.spoteditor.backend.modules.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

}
