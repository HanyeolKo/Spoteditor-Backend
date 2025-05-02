package com.spoteditor.backend.modules.placebookmark.unit;

import com.spoteditor.backend.modules.placebookmark.entity.PlaceBookmark;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PlaceBookmarkTest {

	@Autowired private UserRepository userRepository;
	@Autowired private PlaceRepository placeRepository;
	@Autowired private PlaceBookmarkRepository bookmarkRepository;

	User user;
	Place place;
	PlaceBookmark bookmark;

	@BeforeEach
	void beforeEach() {
		user = User.builder()
				.name("test")
				.email("test@example.com")
				.build();

		place = Place.builder()
				.name("test")
				.build();

		bookmark = PlaceBookmark.builder()
				.user(user)
				.place(place)
				.build();

		userRepository.save(user);
		placeRepository.save(place);
		bookmarkRepository.save(bookmark);
	}

	@AfterEach
	void afterEach() {
		bookmarkRepository.deleteAll();
		placeRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("빌더 패턴을 사용하여 북마크 객체를 생성할 수 있다.")
	void 빌더_패턴을_사용하여_북마크_객체를_생성할_수_있다() {

		// given & when
		PlaceBookmark newBookmark = PlaceBookmark.builder()
				.place(place)
				.user(user)
				.build();

		PlaceBookmark savedBookmark = bookmarkRepository.save(newBookmark);

		// then
		assertThat(bookmarkRepository.count()).isEqualTo(2);
		assertThat(savedBookmark.toString()).isNotNull();
	}
}