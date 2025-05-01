package com.spoteditor.backend.modules.placebookmark.integration;

import com.spoteditor.backend.global.exception.PlaceBookmarkException;
import com.spoteditor.backend.modules.placebookmark.controller.dto.PlaceBookmarkRequest;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.image.repository.PlaceImageRepository;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.placebookmark.service.PlaceBookmarkService;
import com.spoteditor.backend.modules.placebookmark.service.dto.PlaceBookmarkCommand;
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

import static com.spoteditor.backend.global.response.ErrorCode.BOOKMARK_ALREADY_EXIST;
import static com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PlaceBookmarkServiceTest {

	@Autowired private PlaceBookmarkRepository bookmarkRepository;
	@Autowired private PlaceRepository placeRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private PlaceImageRepository placeImageRepository;
	@Autowired private PlaceLogRepository logRepository;

	@Autowired private PlaceBookmarkService bookmarkService;

	User user;
	Place place;
	PlaceImage image;
	PlaceLog log;

	@BeforeEach
	void beforeEach() {

		user = User.builder()
				.name("test")
				.email("test@example.com")
				.build();

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

	// ✅ 공간 북마크 요청 처리
	@Test
	@DisplayName("사용자는 공간에 대한 북마크 처리를 요청할 수 있다.")
	void 사용자는_공간에_대한_북마크_처리를_요청할_수_있다() {

		// given
		Long userId = user.getId();
		Long placeId = place.getId();

		PlaceBookmarkRequest req = PlaceBookmarkRequest.builder()
				.placeId(placeId)
				.build();
		PlaceBookmarkCommand commandDto = req.toCommandDto();

		// when
		bookmarkService.addPlaceBookmark(userId, commandDto);

		// then
		assertThat(bookmarkRepository.count()).isEqualTo(1);
	}

	// ✅ 공간 북마크 취소 처리
	@Test
	@DisplayName("사용자는 공간에 대한 북마크 처리를 취소할 수 있다.")
	void 사용자는_공간에_대한_북마크_처리를_취소할_수_있다() {

		// given
		Long userId = user.getId();
		Long placeId = place.getId();

		PlaceBookmarkRequest req = PlaceBookmarkRequest.builder()
				.placeId(placeId)
				.build();
		PlaceBookmarkCommand commandDto = req.toCommandDto();
		bookmarkService.addPlaceBookmark(userId, commandDto);	// 북마크 처리 추가

		// when
		bookmarkService.removePlaceBookmark(userId, commandDto);

		// then
		assertThat(bookmarkRepository.count()).isZero();
	}

	// ✅ 중복 북마크 예외 처리
	@Test
	@DisplayName("공간에 대해 중복으로 북마크 처리를 할 수 없다.")
	void 공간에_대해_중복으로_북마크_처리를_할_수_없다() {

		// given
		Long userId = user.getId();
		Long placeId = place.getId();

		PlaceBookmarkRequest req = PlaceBookmarkRequest.builder()
				.placeId(placeId)
				.build();
		PlaceBookmarkCommand commandDto = req.toCommandDto();
		bookmarkService.addPlaceBookmark(userId, commandDto);

		// when & then
		assertThatThrownBy(() -> {
			bookmarkService.addPlaceBookmark(userId, commandDto);
		})
				.isInstanceOf(PlaceBookmarkException.class)
				.extracting("errorCode")
				.isEqualTo(BOOKMARK_ALREADY_EXIST);
	}
}