package com.spoteditor.backend.modules.notification.repository;

import com.spoteditor.backend.modules.notification.entity.Notification;
import com.spoteditor.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    @Modifying
    @Query("DELETE FROM Notification noti WHERE noti.fromUser = :user OR noti.toUser = :user")
    void deleteAllByUser(@Param("user") User user);
}
