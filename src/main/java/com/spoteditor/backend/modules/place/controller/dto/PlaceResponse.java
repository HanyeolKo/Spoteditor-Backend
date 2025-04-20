package com.spoteditor.backend.modules.place.controller.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.place.entity.Category;
import lombok.Builder;

@Builder
public record PlaceResponse(

		Long placeId,
		String author,
		String name,
		String description,
		Address address,
		Category category,
		PlaceImageResponse image
) {
}
