package com.spoteditor.backend.modules.placelog.service.dto;

import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogPlaceUpdateRequest;

import java.util.Collections;
import java.util.List;

public record PlaceLogPlaceUpdateCommand (
    Long id,
    String description,
    List<Long> deleteImageIds,
    List<String> originalFiles,
    List<String> uuids
) {
    public static PlaceLogPlaceUpdateCommand from(PlaceLogPlaceUpdateRequest request) {
        return new PlaceLogPlaceUpdateCommand(
                request.id(),
                request.description() != null ? request.description() : null,
                request.deleteImageIds() != null ? request.deleteImageIds() : Collections.emptyList(),
                request.originalFiles() != null ? request.originalFiles() : Collections.emptyList(),
                request.uuids() != null ? request.uuids() : Collections.emptyList()
        );
    }
}
