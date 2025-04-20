package com.spoteditor.backend.modules.tag.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.modules.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.QPlaceLogTagMapping.placeLogTagMapping;
import static com.spoteditor.backend.modules.tag.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tag> findTagsByPlaceLogId(Long placeLogId) {
        return queryFactory
                .select(tag)
                .from(placeLogTagMapping)
                .join(placeLogTagMapping.tag, tag)
                .where(placeLogTagMapping.placeLog.id.eq(placeLogId))
                .fetch();
    }

    @Override
    public List<Tag> findByNameIn(List<String> tags) {
        return queryFactory
                .selectFrom(tag)
                .where(tag.name.in(tags))
                .fetch();
    }
}
