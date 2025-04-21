package com.spoteditor.backend.modules.placelog.service.dto;

import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogPlaceRegisterRequest;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogUpdateRequest;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.tag.dto.TagDto;

import java.util.Collections;
import java.util.List;

public record PlaceLogUpdateCommand (
    String name,
    String description,
    String originalFile,
    String uuid,
    PlaceLogStatus status,
    List<TagDto> deleteTags,
    List<TagDto> addTags,
    List<Long> deletePlaceIds,
    List<PlaceLogPlaceRegisterRequest> addPlaces,
    List<PlaceLogPlaceUpdateCommand> updatePlaces
) {
    public static PlaceLogUpdateCommand from(PlaceLogUpdateRequest request) {
        return new PlaceLogUpdateCommand(
                request.name() != null ? request.name() : null,
                request.description() != null ? request.description() : null,
                request.originalFile() != null ? request.originalFile() : null,
                request.uuid() != null ? request.uuid() : null,
                request.status() != null ? request.status() : null,
                request.deleteTags() != null ? request.deleteTags() : Collections.emptyList(),
                request.addTags() != null ? request.addTags() : Collections.emptyList(),
                request.deletePlaceIds() != null ? request.deletePlaceIds() : Collections.emptyList(),
                request.addPlaces() != null ? request.addPlaces() : Collections.emptyList(),
                request.updatePlaces() != null ? request.updatePlaces().stream()
                        .map(PlaceLogPlaceUpdateCommand::from)
                        .toList() : Collections.emptyList()
        );
    }
}
