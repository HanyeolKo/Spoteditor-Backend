package com.spoteditor.backend.modules.place.repository;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.place.controller.dto.PlaceResponse;
import com.spoteditor.backend.modules.place.entity.Place;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepositoryCustom {

	CustomPageResponse<PlaceResponse> findAllPlace(CustomPageRequest pageRequest);

	CustomPageResponse<PlaceResponse> findMyBookmarkPlace(Long userId, CustomPageRequest pageRequest);

	List<Place> findByIdIn(List<Long> placeIds);

	List<Place> findAllPlacesByUserId(Long userId);

}
