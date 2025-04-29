package com.spoteditor.backend.modules.placebookmark.integration;

import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.image.repository.PlaceImageRepository;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.placebookmark.service.dto.PlaceBookmarkCommand;
import com.spoteditor.backend.modules.placebookmark.service.facade.PlaceBookmarkFacade;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlaceBookmarkFacadeTest {

	@Autowired private PlaceRepository placeRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private PlaceImageRepository placeImageRepository;
	@Autowired private PlaceLogRepository logRepository;
	@Autowired private PlaceBookmarkRepository bookmarkRepository;

	@Autowired private PlaceBookmarkFacade bookmarkFacade;

	User user;
	Place place;
	PlaceImage image;
	PlaceLog log;

	int threads = 200;

	@BeforeEach
	void beforeEach() {

		for (int i = 1; i <= threads; i++) {
			User user = User.builder()
					.name("test" + i)
					.email("test" + i + "@example.com")
					.build();
			userRepository.save(user);
		}

		user = User.builder()
				.name("singleTestUser")
				.email("singleTestUser@example.com")
				.build();
		userRepository.save(user);

		place = Place.builder()
				.name("test")
				.build();

		image = PlaceImage.builder()
				.place(place)
				.originalFile("test")
				.storedFile("test")
				.build();

		log = PlaceLog.builder()
				.name("test")
				.user(user)
				.placeLogImage(image)
				.status(PUBLIC)
				.build();

		userRepository.save(user);
		placeRepository.save(place);
		placeImageRepository.save(image);
		logRepository.save(log);
	}

	@AfterEach
	void afterEach() {

		bookmarkRepository.deleteAll();
		logRepository.deleteAll();
		placeImageRepository.deleteAll();
		placeRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("여러 사용자가 동시에 하나의 공간에 대해 북마크 요청을 할 수 있다.")
	void 여러_사용자가_동시에_하나의_공간에_대해_북마크_요청을_할_수_있다() throws InterruptedException {

		// given
		Long placeId = place.getId();
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(threads);
		PlaceBookmarkCommand command = new PlaceBookmarkCommand(placeId);

		List<User> list = userRepository.findAll();

		// when
		for (int i = 0; i < threads; i++) {
			User user = list.get(i);
			Long userId = user.getId();
			executorService.submit(() -> {
				try {
					bookmarkFacade.addPlaceBookmark(userId, command);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// then
		assertThat(bookmarkRepository.count()).isEqualTo(threads);
	}

	@Test
	@DisplayName("여러 사용자가 동시에 하나의 공간에 대해 북마크 취소를 할 수 있다.")
	void 여러_사용자가_동시에_하나의_공간에_대해_북마크_취소를_할_수_있다() throws InterruptedException {

		// given
		Long placeId = place.getId();
		PlaceBookmarkCommand command = new PlaceBookmarkCommand(placeId);

		List<User> list = userRepository.findAll();

		for (int i = 0; i < threads; i++) {
			User user = list.get(i);
			Long userId = user.getId();
			bookmarkFacade.addPlaceBookmark(userId, command);
		}

		assertThat(bookmarkRepository.count()).isEqualTo(threads);

		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(threads);

		// when
		for (int i = 0; i < threads; i++) {
			User user = list.get(i);
			Long userId = user.getId();
			executorService.submit(() -> {
				try {
					bookmarkFacade.removePlaceBookmark(userId, command);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// then
		assertThat(bookmarkRepository.count()).isZero();
	}
}