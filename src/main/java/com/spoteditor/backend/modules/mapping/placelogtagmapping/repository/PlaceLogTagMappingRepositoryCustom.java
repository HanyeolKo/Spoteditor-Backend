package com.spoteditor.backend.modules.mapping.placelogtagmapping.repository;


import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;

import java.util.List;

public interface PlaceLogTagMappingRepositoryCustom {

    List<PlaceLogTagMapping> findByPlaceLogId(Long placeLogId);

    List<PlaceLogTagMapping> findByPlaceLogAndTagIn(Long placeLogId, List<Long> tagIds);
}
