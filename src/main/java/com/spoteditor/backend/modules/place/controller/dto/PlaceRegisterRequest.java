package com.spoteditor.backend.modules.place.controller.dto;

import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.place.entity.Category;
import com.spoteditor.backend.modules.place.service.dto.PlaceRegisterCommand;
import lombok.Builder;

@Builder
public record PlaceRegisterRequest(
		String name,
		String description,
		String originalFile,
		String uuid,
		Address address,
		Category category
) {

	public PlaceRegisterCommand from() {
		return new PlaceRegisterCommand(
				this.name,
				this.description,
				this.originalFile,
				this.uuid,
				this.address,
				this.category
		);
	}
}
