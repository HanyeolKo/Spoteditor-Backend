package com.spoteditor.backend.modules.placelog.controller.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.place.entity.Category;
import com.spoteditor.backend.modules.place.entity.Place;

import java.util.List;

public record PlaceLogPlaceRegisterResponse(
        Long placeId,
        String name,
        String description,
        Address address,
        Category category,
        List<PlaceImageResponse> images
) {
    public static PlaceLogPlaceRegisterResponse from (Place place) {
        return new PlaceLogPlaceRegisterResponse (
                place.getId(),
                place.getName(),
                place.getDescription(),
                place.getAddress(),
                place.getCategory(),
                place.getPlaceImages()
                        .stream()
                        .map(PlaceImageResponse::from)
                        .toList()
        );
    }
}