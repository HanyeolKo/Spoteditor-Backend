package com.spoteditor.backend.modules.placelog.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;

import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogWithBookmark;

import java.util.List;

public interface PlaceLogRepositoryCustom {

    CustomPageResponse<PlaceLogListResponse> findAllPlace(CustomPageRequest pageRequest, OrderSpecifier<?> orderSpecifier);

    CustomPageResponse<PlaceLogListResponse> findMyPlaceLog(Long userId, CustomPageRequest pageRequest);

    CustomPageResponse<PlaceLogListResponse> findOtherPlaceLog(Long userId, CustomPageRequest pageRequest);

    CustomPageResponse<PlaceLogListResponse> findMyBookmarkPlaceLog(Long userId, CustomPageRequest pageRequest);

    CustomPageResponse<PlaceLogListResponse> searchBySidoBname(CustomPageRequest pageRequest, String sido, String bname);

    CustomPageResponse<PlaceLogListResponse> searchByName(CustomPageRequest pageRequest, String name);

    List<PlaceLogWithBookmark> findPlaceLogWithBookmarkCount(int limit, int offset);
}
