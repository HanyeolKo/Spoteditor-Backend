package com.spoteditor.backend.modules.place.service.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.place.entity.Category;
import com.spoteditor.backend.modules.place.entity.Place;

import java.util.List;
import java.util.stream.Collectors;

public record PlaceRegisterResult(

		Long userId,
		Long placeId,
		String name,
		String description,
		int bookmark,
		Address address,
		Category category,
		List<PlaceImageResponse> images
) {

	public static PlaceRegisterResult from(Place place) {
		return new PlaceRegisterResult(
				place.getUser().getId(),
				place.getId(),
				place.getName(),
				place.getDescription(),
				place.getBookmark(),
				place.getAddress(),
				place.getCategory(),
				place.getPlaceImages()
						.stream()
						.map(PlaceImageResponse::from)
						.collect(Collectors.toList())
		);
	}
}
