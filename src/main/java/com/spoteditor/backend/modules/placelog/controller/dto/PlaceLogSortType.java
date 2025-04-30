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
    RECENT("created_at", placeLog.createdAt, Sort.Direction.ASC),
    /**
     * 인기순 정렬
     */
    POPULARITY("popularity_score", placeLog.popularityScore, Sort.Direction.DESC);

    private final String columnName;        // 실제 DB 컬럼명
    private final ComparableExpressionBase<?> path;     // QueryDSL 필드
    private final Sort.Direction defaultDirection;      // 정렬

    PlaceLogSortType(String columnName, ComparableExpressionBase<?> path, Sort.Direction defaultDirection) {
        this.columnName = columnName;
        this.path = path;
        this.defaultDirection = defaultDirection;
    }

    public OrderSpecifier<?> getOrderSpecifier(Sort.Direction direction) {

        Sort.Direction directionToUse = (direction != null) ? direction : defaultDirection;
        return directionToUse.isAscending() ? path.asc() : path.desc();
    }
}
