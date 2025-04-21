package com.spoteditor.backend.modules.placelog.service.dto;

import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogPlaceRegisterRequest;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogRegisterRequest;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.tag.dto.TagDto;

import java.util.List;

public record PlaceLogRegisterCommand (
        String name,
        String description,
        String originalFile,
        String uuid,
        PlaceLogStatus status,
        List<TagDto> tags,
        List<PlaceLogPlaceRegisterRequest> placeRegisterRequests
) {
    public static PlaceLogRegisterCommand from(PlaceLogRegisterRequest request) {
        return new PlaceLogRegisterCommand(
                request.name(),
                request.description(),
                request.originalFile(),
                request.uuid(),
                request.status(),
                request.tags(),
                request.places()
        );
    }
}
