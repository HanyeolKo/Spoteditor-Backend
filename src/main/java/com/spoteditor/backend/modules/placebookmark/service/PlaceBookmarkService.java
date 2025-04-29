package com.spoteditor.backend.modules.placebookmark.service;

import com.spoteditor.backend.global.exception.PlaceBookmarkException;
import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.placebookmark.entity.PlaceBookmark;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.placebookmark.service.dto.PlaceBookmarkCommand;
import com.spoteditor.backend.global.exception.PlaceException;
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
public class PlaceBookmarkService {

	private final PlaceBookmarkRepository placeBookmarkRepository;
	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	@Transactional
	public void addPlaceBookmark(Long userId, PlaceBookmarkCommand command) {

		Place place = placeRepository.findById(command.placeId())
				.orElseThrow(() -> new PlaceException(NOT_FOUND_PLACE));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		if (placeBookmarkRepository.findByUserIdAndPlaceId(userId, command.placeId()).isPresent()) {
			throw new PlaceBookmarkException(BOOKMARK_ALREADY_EXIST);
		} else {
			PlaceBookmark bookmark = PlaceBookmark.builder()
					.user(user)
					.place(place)
					.build();
			placeBookmarkRepository.save(bookmark);
			place.increaseBookmark();
		}
	}

	@Transactional
	public void removePlaceBookmark(Long userId, PlaceBookmarkCommand command) {

		Place place = placeRepository.findById(command.placeId())
				.orElseThrow(() -> new PlaceException(NOT_FOUND_PLACE));
		PlaceBookmark bookmark = placeBookmarkRepository.findByUserIdAndPlaceId(userId, command.placeId())
				.orElseThrow(() -> new PlaceBookmarkException(NOT_FOUND_PLACE_BOOKMARK));
		place.decreaseBookmark();
		placeBookmarkRepository.delete(bookmark);
	}
}
