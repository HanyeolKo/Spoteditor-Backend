package com.spoteditor.backend.modules.place.controller.validator;

import com.spoteditor.backend.global.exception.PlaceException;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.spoteditor.backend.global.response.ErrorCode.NOT_FOUND_PLACE;

@Component
@RequiredArgsConstructor
public class PlaceRequestValidator implements Validator {

	private final PlaceRepository placeRepository;

	public void validatePlaceExists(Long placeId) {
		if (!placeRepository.existsById(placeId)) {
			throw new PlaceException(NOT_FOUND_PLACE);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Place.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		// 다운캐스팅 대신 instanceOf 사용
		if (!(target instanceof Place place)) {
			throw new IllegalArgumentException(target.getClass().toString());
		}
		validatePlaceExists(place.getId());
	}
}