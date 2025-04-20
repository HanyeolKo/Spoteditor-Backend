package com.spoteditor.backend.modules.place.service;

import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.modules.image.service.PlaceImageService;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterCommand;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterResult;
import com.spoteditor.backend.modules.user.entity.User;
import com.spoteditor.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spoteditor.backend.global.response.ErrorCode.DELETED_USER;
import static com.spoteditor.backend.global.response.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final PlaceImageService imageService;
	private final UserRepository userRepository;

	/**
	 *
	 * @param userId
	 * @param command
	 * @return
	 */
	@Override
	@Transactional
	public PlaceRegisterResult addPlace(Long userId, PlaceRegisterCommand command) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserException(NOT_FOUND_USER));

		if(user.isDeleted()) {
			throw new UserException(DELETED_USER);
		}

		Place savedPlace = placeRepository.save(command.toEntity(user));
		imageService.upload(command.originalFile(), command.uuid(), savedPlace.getId());
		return PlaceRegisterResult.from(savedPlace);
	}
}
