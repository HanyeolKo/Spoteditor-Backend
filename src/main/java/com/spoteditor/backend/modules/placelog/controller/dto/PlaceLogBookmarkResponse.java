package com.spoteditor.backend.modules.placelog.controller.dto;

public record PlaceLogBookmarkResponse(
	Long placeId,
	boolean isBookmarked
) {
}
