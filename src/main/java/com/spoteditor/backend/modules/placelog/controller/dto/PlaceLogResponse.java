package com.spoteditor.backend.modules.placelog.controller.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogResult;
import com.spoteditor.backend.modules.tag.dto.TagDto;

import java.util.List;

public record PlaceLogResponse(
        Long placeLogId,
        Long userId,
        String userName,
        String userImage,
        boolean isFollowing,
        String name,
        String description,
        PlaceImageResponse image,
        Address address,
        PlaceLogStatus status,
        long views,
        List<TagDto> tags,
        List<PlaceLogPlaceRegisterResponse> places
) {
    public static PlaceLogResponse from(PlaceLogResult result) {
        return new PlaceLogResponse(
                result.placeLog().getId(),
                result.placeLog().getUser().getId(),
                result.placeLog().getUser().getName(),
                result.placeLog().getUser().getImageUrl(),
                false,
                result.placeLog().getName(),
                result.placeLog().getDescription(),
                PlaceImageResponse.from(result.placeLog().getPlaceLogImage()),
                result.placeLog().getAddress(),
                result.placeLog().getStatus(),
                result.placeLog().getViews(),
                result.tags().stream()
                        .map(TagDto::from)
                        .toList(),
                result.places().stream()
                        .map(PlaceLogPlaceRegisterResponse::from)
                        .toList()
        );
    }

    public static PlaceLogResponse from(PlaceLogResult result, boolean isFollowing) {
        return new PlaceLogResponse(
                result.placeLog().getId(),
                result.placeLog().getUser().getId(),
                result.placeLog().getUser().getName(),
                result.placeLog().getUser().getImageUrl(),
                isFollowing,
                result.placeLog().getName(),
                result.placeLog().getDescription(),
                PlaceImageResponse.from(result.placeLog().getPlaceLogImage()),
                result.placeLog().getAddress(),
                result.placeLog().getStatus(),
                result.placeLog().getViews(),
                result.tags().stream()
                        .map(TagDto::from)
                        .toList(),
                result.places().stream()
                        .map(PlaceLogPlaceRegisterResponse::from)
                        .toList()
        );
    }
}
