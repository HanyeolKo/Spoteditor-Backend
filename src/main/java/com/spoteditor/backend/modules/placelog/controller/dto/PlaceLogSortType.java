package com.spoteditor.backend.modules.placelog.controller.dto;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import static com.spoteditor.backend.modules.placelog.entity.QPlaceLog.placeLog;

@Getter
public enum PlaceLogSortType {
    /**
     * 최신순 정렬
     */
    RECENT,
    /**
     * 인기순 정렬
     */
    POPULARITY;

}
