package com.spoteditor.backend.modules.placelog.controller.dto;

import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.tag.dto.TagDto;

import java.util.List;

public record PlaceLogRegisterRequest (
        String name,
        String description,
        String originalFile,
        String uuid,
        PlaceLogStatus status,
        List<TagDto> tags,
        List<PlaceLogPlaceRegisterRequest> places
) {
}
