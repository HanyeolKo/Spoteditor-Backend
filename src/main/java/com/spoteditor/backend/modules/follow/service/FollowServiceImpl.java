package com.spoteditor.backend.modules.follow.service;

import com.spoteditor.backend.modules.follow.controller.dto.FollowRequest;
import com.spoteditor.backend.modules.follow.entity.Follow;
import com.spoteditor.backend.modules.notification.dto.NotificationMessage;
import com.spoteditor.backend.modules.notification.entity.Notification;
import com.spoteditor.backend.modules.notification.event.NotificationEventDto;
import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.global.exception.FollowException;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.notification.handler.NotificationEventHandler;
import com.spoteditor.backend.modules.notification.repository.NotificationRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spoteditor.backend.global.response.ErrorCode.*;
import static com.spoteditor.backend.modules.notification.entity.NotificationType.FOLLOW;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final NotificationEventHandler handler;
	private final NotificationRepository notificationRepository;

	@Transactional
	public void saveFollow(Long userId, FollowRequest request) {

		log.info("Follow Thread Name:{}", Thread.currentThread().getName());
		User follower = findUserById(userId);
		User following = findUserById(request.userId());

		followRepository.findFollowByFollowerAndFollowing(follower, following)
				.ifPresent(follow -> { throw new FollowException(DUPLICATED_FOLLOW); });

		Follow follow = Follow.builder()
				.follower(follower)
				.following(following)
				.build();

		followRepository.save(follow);

		String message = NotificationMessage.formatFollowMessage(follower.getName());
		NotificationEventDto eventDto = NotificationEventDto.from(
				follower, following, FOLLOW, message
		);

		handler.handleNotificationEvent(eventDto);	// 커밋 성공 시에 비동기로 처리
		Notification entity = eventDto.toEntity(follower, following, FOLLOW, message);
		notificationRepository.save(entity);
	}

	@Transactional
	public void removeFollow(Long userId, FollowRequest request) {

		User follower = findUserById(userId);
		User following = findUserById(request.userId());
		followRepository.deleteByFollowerAndFollowing(follower, following);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));
	}
}