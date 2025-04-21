package com.spoteditor.backend.modules.notification.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.modules.notification.controller.dto.NotificationListDto;
import com.spoteditor.backend.modules.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.notification.entity.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<NotificationListDto> notificationList(Long userId) {
		return queryFactory
				.select(Projections.constructor(NotificationListDto.class,
				notification.id,					// 알림 PK
						notification.fromUser.imageUrl,		// 프로필 이미지
						notification.message,				// 메시지
						notification.notificationType,		// 알림 타입
						notification.createdAt,				// 알림 발송 시간
						notification.isRead					// 알림 읽음 여부
				))
				.from(notification)
				.where(notification.toUser.id.eq(userId))
				.orderBy(notification.createdAt.desc())
				.fetch();
	}

	@Override
	public List<Notification> findAllByUserIdAndUnread(Long userId) {
		return queryFactory
				.selectFrom(notification)
				.where(notification.toUser.id.eq(userId), notification.isRead.eq(false))
				.orderBy(notification.toUser.id.desc())
				.fetch();
	}

	@Override
	public List<Notification> findAllByUserIdAndRead(Long userId) {
		return queryFactory
				.selectFrom(notification)
				.where(notification.toUser.id.eq(userId), notification.isRead.eq(true))
				.orderBy(notification.toUser.id.desc())
				.fetch();
	}

	@Modifying(clearAutomatically = true)
	@Override
	public void updateNotificationRead(Long userId) {
		queryFactory.update(notification)
				.set(notification.isRead, true)
				.where(notification.toUser.id.eq(userId))
				.execute();
	}
}