package com.spoteditor.backend.bookmark.service;

import com.spoteditor.backend.modules.bookmark.repository.BookmarkRepository;
import com.spoteditor.backend.modules.bookmark.service.dto.BookmarkCommand;
import com.spoteditor.backend.modules.bookmark.service.facade.BookmarkFacade;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
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

@SpringBootTest
@ActiveProfiles("test")
class BookmarkServiceTest {

	@Autowired private PlaceRepository placeRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private BookmarkFacade bookmarkFacade;
	@Autowired private BookmarkRepository bookmarkRepository;

	private Place savedPlace;
	private List<User> savedUsers = new ArrayList<>();

	@BeforeEach
	void setUp() {
		Place place = Place.builder()
				.name("공간")
				.build();
		savedPlace = placeRepository.save(place);

		for (int i = 0; i < 200; i++) {
			User user = User.builder()
					.name("test" + i)
					.build();
			savedUsers.add(userRepository.save(user));
		}
	}

	@AfterEach
	void tearDown() {
		bookmarkRepository.deleteAll();
		placeRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("북마크추가_분산락_적용_동시성200명_테스트")
	void 북마크추가_분산락_적용_동시성200명_테스트() throws InterruptedException {
		// given
		int numberOfThreads = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		BookmarkCommand command = new BookmarkCommand(savedPlace.getId());

		for (int i = 0; i < numberOfThreads; i++) {
			final Long userId = savedUsers.get(i).getId();
			executorService.submit(() -> {
				try {
					bookmarkFacade.addBookmark(userId, command);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		long count = bookmarkRepository.count();
		assertThat(count).isEqualTo(numberOfThreads);
	}

	@Test
	@DisplayName("북마크삭제_분산락_적용_동시성200명_테스트")
	void 북마크삭제_분산락_적용_동시성200명_테스트() throws InterruptedException {
		// given
		int numberOfThreads = 200;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		BookmarkCommand command = new BookmarkCommand(savedPlace.getId());

		for (int i = 0; i < numberOfThreads; i++) {
			final Long userId = savedUsers.get(i).getId();
			bookmarkFacade.addBookmark(userId, command);
		}

		for (int i = 0; i < numberOfThreads; i++) {
			final Long userId = savedUsers.get(i).getId();
			executorService.submit(() -> {
				try {
					bookmarkFacade.removeBookmark(userId, command);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		long count = bookmarkRepository.count();
		assertThat(count).isEqualTo(0);
	}
}
