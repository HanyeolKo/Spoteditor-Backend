package com.spoteditor.backend.modules.placelog.controller.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.place.entity.Address;

public record PlaceLogListResponse (
        Long placeLogId,
        String author,
        String name,
        PlaceImageResponse image,
        Address address,
        long views
) {
}
