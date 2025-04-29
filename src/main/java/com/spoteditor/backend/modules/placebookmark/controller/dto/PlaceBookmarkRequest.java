package com.spoteditor.backend.modules.placebookmark.controller.dto;

import com.spoteditor.backend.modules.placebookmark.service.dto.PlaceBookmarkCommand;
import lombok.Builder;

@Builder
public record PlaceBookmarkRequest(Long placeId) {

	public PlaceBookmarkCommand toCommandDto() {
		return new PlaceBookmarkCommand(this.placeId);
	}
}
