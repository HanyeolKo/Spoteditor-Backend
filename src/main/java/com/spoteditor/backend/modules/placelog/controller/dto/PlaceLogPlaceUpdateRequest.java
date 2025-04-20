package com.spoteditor.backend.modules.placelog.controller.dto;

import jakarta.annotation.Nullable;

import java.util.List;

public record PlaceLogPlaceUpdateRequest (
        Long id,
        @Nullable String description,
        @Nullable List<Long> deleteImageIds,
        @Nullable List<String> originalFiles,
        @Nullable List<String> uuids
) {
}