package com.spoteditor.backend.follow.repository;

import com.spoteditor.backend.config.QuerydslConfig;
import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.follow.controller.dto.FollowResponse;
import com.spoteditor.backend.modules.follow.entity.Follow;
import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@DataJpaTest	// @Transactional 어노테이션이 붙어 있어 롤백됨
class FollowRepositoryTest {

	@Autowired private FollowRepository followRepository;
	@Autowired private UserRepository userRepository;

	private List<User> users = new ArrayList<>();

	@BeforeEach
	void beforeEach() {
		for (int i = 0; i <= 101; i++) {
			User user = User.builder()
					.email("user" + i + "@test.com")
					.name("테스트 " + i)
					.build();
			users.add(userRepository.save(user));
		}

		User mainUser = users.get(0);
		List<Follow> follows = new ArrayList<>();

		for (int i = 1; i <= 101; i++) {
			Follow follow1 = Follow.builder()
					.follower(mainUser)
					.following(users.get(i))
					.build();
			follows.add(follow1);

			Follow follow2 = Follow.builder()
					.follower(users.get(i))
					.following(mainUser)
					.build();
			follows.add(follow2);
		}
		followRepository.saveAll(follows);
	}

	@AfterEach
	void afterEach() {
		followRepository.deleteAll();
        userRepository.deleteAll();
	}

	@Test
	@DisplayName("내가 팔로우한 사람 리스트를 조회할 때 성능 저하 문제가 발생하지 않아야 한다")
	void 내가_팔로우한_사람_리스트를_조회할_때_성능_저하_문제가_발생하지_않아야_한다() {
		// given
		User mainUser = users.get(0);
		CustomPageRequest req = new CustomPageRequest();

		req.setDirection(Sort.Direction.ASC);
		req.setSize(20);
		req.setPage(1);

		// when
		CustomPageResponse<FollowResponse> result = followRepository.findAllFollowing(mainUser.getId(), req);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isNotEmpty();
		assertThat(result.getContent().size()).isEqualTo(20);
	}

	@Test
	@DisplayName("나를 팔로우한 사람_리스트를_조회할_때_성능_저하_문제가_발생하지_않아야_한다")
	void 나를_팔로우한_사람_리스트를_조회할_때_성능_저하_문제가_발생하지_않아야_한다() {
		// given
		User mainUser = users.get(0);
		CustomPageRequest req = new CustomPageRequest();

		req.setDirection(Sort.Direction.ASC);
		req.setSize(20);
		req.setPage(1);

		// when
		CustomPageResponse<FollowResponse> result = followRepository.findAllFollower(mainUser.getId(), req);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isNotEmpty();
		assertThat(result.getContent().size()).isEqualTo(20);
	}
}