package com.spoteditor.backend.modules.placebookmark.service.dto;

import lombok.Builder;

@Builder
public record PlaceBookmarkCommand(Long placeId) {
}
