package com.spoteditor.backend.modules.place.controller.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.place.entity.Category;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterResult;

import java.util.List;

public record PlaceRegisterResponse(

		Long userId,
		Long placeId,
		String name,
		String description,
		int bookmark,
		Address address,
		Category category,
		List<PlaceImageResponse> images
) {

	public static PlaceRegisterResponse from(PlaceRegisterResult result) {
		return new PlaceRegisterResponse(
				result.userId(),
				result.placeId(),
                result.name(),
                result.description(),
				result.bookmark(),
                result.address(),
				result.category(),
				result.images()
        );
	}
}
