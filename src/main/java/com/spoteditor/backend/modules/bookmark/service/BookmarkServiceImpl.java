package com.spoteditor.backend.modules.bookmark.service;

import com.spoteditor.backend.modules.bookmark.entity.Bookmark;
import com.spoteditor.backend.modules.bookmark.repository.BookmarkRepository;
import com.spoteditor.backend.modules.bookmark.service.dto.BookmarkCommand;
import com.spoteditor.backend.global.exception.BookmarkException;
import com.spoteditor.backend.global.exception.PlaceException;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spoteditor.backend.global.response.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

	private final BookmarkRepository bookmarkRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	/**
	 *
	 * @param userId
	 * @param command
	 */
	@Override
	@Transactional
	public void addBookmark(Long userId, BookmarkCommand command) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		if(user.isDeleted()) {
			throw new UserException(DELETED_USER);
		}

		Place place = placeRepository.findById(command.placeId())
				.orElseThrow(() -> new PlaceException(NOT_FOUND_PLACE));

		if (bookmarkRepository.findByUserIdAndPlaceId(userId, command.placeId()).isPresent()) {
			throw new BookmarkException(BOOKMARK_ALREADY_EXIST);
		} else {
			Bookmark bookmark = Bookmark.builder()
					.user(user)
					.place(place)
					.build();
			bookmarkRepository.save(bookmark);
			place.increaseBookmark();
		}
	}

	/**
	 *
	 * @param userId
	 * @param command
	 */
	@Override
	@Transactional
	public void removeBookmark(Long userId, BookmarkCommand command) {

		Place place = placeRepository.findById(command.placeId())
				.orElseThrow(() -> new PlaceException(NOT_FOUND_PLACE));

		Bookmark bookmark = bookmarkRepository.findByUserIdAndPlaceId(userId, command.placeId())
				.orElseThrow(() -> new BookmarkException(NOT_FOUND_BOOKMARK));

		place.decreaseBookmark();
		bookmarkRepository.delete(bookmark);
	}

	@Override
	public boolean existsByUserIdAndPlaceId(Long userId, Long placeId) {
		return bookmarkRepository.existsByUserAndPlace(userId, placeId);
	}
}
