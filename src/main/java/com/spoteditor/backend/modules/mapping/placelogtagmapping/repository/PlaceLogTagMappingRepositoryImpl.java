package com.spoteditor.backend.modules.mapping.placelogtagmapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.QPlaceLogTagMapping.placeLogTagMapping;

@Repository
@RequiredArgsConstructor
public class PlaceLogTagMappingRepositoryImpl implements PlaceLogTagMappingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaceLogTagMapping> findByPlaceLogId(Long placeLogId) {
        return queryFactory
                .selectFrom(placeLogTagMapping)
                .where(placeLogTagMapping.placeLog.id.eq(placeLogId))
                .fetch();
    }

    @Override
    public List<PlaceLogTagMapping> findByPlaceLogAndTagIn(Long placeLogId, List<Long> tagIds) {
        return queryFactory
                .selectFrom(placeLogTagMapping)
                .where(placeLogTagMapping.placeLog.id.eq(placeLogId))
                .where(placeLogTagMapping.tag.id.in(tagIds))
                .fetch();
    }
}
