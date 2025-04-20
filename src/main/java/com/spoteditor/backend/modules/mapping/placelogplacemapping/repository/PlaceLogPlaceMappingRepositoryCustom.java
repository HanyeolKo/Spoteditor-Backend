package com.spoteditor.backend.modules.mapping.placelogplacemapping.repository;

import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;

import java.util.List;

public interface PlaceLogPlaceMappingRepositoryCustom {

    List<PlaceLogPlaceMapping> findByPlaceLogId(Long placeLogId);

    List<PlaceLogPlaceMapping> findByPlaceLogAndPlaceIn(Long placeLogId, List<Long> placeIds);

    boolean exists(Long placeLogId, Long placeId);
}
