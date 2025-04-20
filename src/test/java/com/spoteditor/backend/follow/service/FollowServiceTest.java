package com.spoteditor.backend.follow.service;

import com.spoteditor.backend.modules.follow.controller.dto.FollowRequest;
import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.modules.follow.service.facade.FollowFacade;
import com.spoteditor.backend.global.exception.FollowException;
import com.spoteditor.backend.modules.notification.repository.NotificationRepository;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class FollowServiceTest {

	@Autowired private FollowFacade followFacade;
	@Autowired private UserRepository userRepository;
	@Autowired private FollowRepository followRepository;
	@Autowired private NotificationRepository notificationRepository;

	private List<User> savedUsers = new ArrayList<>();

	@BeforeEach
	void setUp() {
		for (int i = 0; i < 200; i++) {
			User user = User.builder()
					.name("test" + i)
					.build();
			savedUsers.add(userRepository.save(user));
		}
	}

	@AfterEach
	void tearDown() {
		notificationRepository.deleteAll();
		followRepository.deleteAll();
        userRepository.deleteAll();
	}

	@Test
	@DisplayName("팔로우추가_분산락_적용_동시성200명_테스트")
	void 팔로우추가_분산락_적용_동시성200명_테스트() throws InterruptedException {
		// given
		int numberOfThreads = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		User targetUser = savedUsers.get(0);

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			final int index = i;
			executorService.submit(() -> {
				try {
					UserIdDto userIdDto = new UserIdDto(savedUsers.get(index).getId());
					FollowRequest followRequest = new FollowRequest(targetUser.getId());
					followFacade.saveFollow(userIdDto.getId(), followRequest);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		// then
		long count = followRepository.count();
		assertThat(count).isEqualTo(numberOfThreads);
	}

	@Test
	@DisplayName("팔로우삭제_분산락_적용_동시성200명_테스트")
	void 팔로우삭제_분산락_적용_동시성200명_테스트() throws InterruptedException {
		// given
		int numberOfThreads = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		User targetUser = savedUsers.get(0);

		for (int i = 0; i < numberOfThreads; i++) {
			final Long userId = savedUsers.get(i).getId();
			FollowRequest followRequest = new FollowRequest(targetUser.getId());
			followFacade.saveFollow(userId, followRequest);
		}

		// when
		for (int i = 0; i < numberOfThreads; i++) {
			final int index = i;
			executorService.submit(() -> {
				try {
					UserIdDto userIdDto = new UserIdDto(savedUsers.get(index).getId());
					FollowRequest followRequest = new FollowRequest(targetUser.getId());
					followFacade.removeFollow(userIdDto.getId(), followRequest);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		// then
		long count = followRepository.count();
		assertThat(count).isEqualTo(0);
	}

	@Test
	@DisplayName("중복 팔로우를 할 수 없다.")
	void 중복_팔로우를_할_수_없다() {
		// given
        User mainUser = savedUsers.get(0);
        User targetUser = savedUsers.get(1);

        FollowRequest followRequest = new FollowRequest(targetUser.getId());

        // when
        UserIdDto userIdDto = new UserIdDto(mainUser.getId());
        followFacade.saveFollow(userIdDto.getId(), followRequest);

		assertThrows(FollowException.class, () -> followFacade.saveFollow(userIdDto.getId(), followRequest));
	}
}