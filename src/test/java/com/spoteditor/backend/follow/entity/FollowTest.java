package com.spoteditor.backend.follow.entity;

import com.spoteditor.backend.global.exception.FollowException;
import com.spoteditor.backend.modules.follow.entity.Follow;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class FollowTest {

	@Autowired private UserRepository userRepository;

	private User user1;
	private User user2;

	@BeforeEach
	void beforeEach() {
		user1 = User.builder()
				.email("user1@example.com")
				.name("user1")
				.build();

		user2 = User.builder()
				.email("user2@example.com")
				.name("user2")
				.build();
		user1 = userRepository.save(user1);
		user2 = userRepository.save(user2);
	}

	@AfterEach
	void afterEach() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("자기 자신을 팔로우할 수 없다.")
	void 자기_자신을_팔로우할_수_없다() {
		Follow follow = Follow.builder()
				.following(user1)
				.follower(user1)
				.build();
		assertThrows(FollowException.class, () -> follow.validateNotSelfFollow(user1, user1));
	}
}