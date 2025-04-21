package com.spoteditor.backend.modules.mapping.placelogplacemapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.QPlaceLogPlaceMapping.placeLogPlaceMapping;

@Repository
@RequiredArgsConstructor
public class PlaceLogPlaceMappingRepositoryImpl implements PlaceLogPlaceMappingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaceLogPlaceMapping> findByPlaceLogId(Long placeLogId) {
        return queryFactory
                .selectFrom(placeLogPlaceMapping)
                .where(placeLogPlaceMapping.placeLog.id.eq(placeLogId))
                .fetch();
    }

    @Override
    public List<PlaceLogPlaceMapping> findByPlaceLogAndPlaceIn(Long placeLogId, List<Long> placeIds) {
        return queryFactory
                .selectFrom(placeLogPlaceMapping)
                .where(placeLogPlaceMapping.placeLog.id.eq(placeLogId))
                .where(placeLogPlaceMapping.place.id.in(placeIds))
                .fetch();
    }

    @Override
    public boolean exists(Long placeLogId, Long placeId) {
        return queryFactory
                .selectOne()
                .from(placeLogPlaceMapping)
                .where(placeLogPlaceMapping.placeLog.id.eq(placeLogId))
                .where(placeLogPlaceMapping.place.id.eq(placeId))
                .fetchFirst() != null;
    }
}
