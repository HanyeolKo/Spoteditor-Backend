package com.spoteditor.backend.modules.mapping.userplacelogmapping.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.spoteditor.backend.modules.mapping.userplacelogmapping.entity.QUserPlaceLogMapping.userPlaceLogMapping;

@Repository
@RequiredArgsConstructor
public class UserPlaceLogMappingRepositoryImpl implements  UserPlaceLogMappingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserIdAndPlaceLogId(Long userId, Long placeLogId) {
        Integer result = queryFactory
                .selectOne()
                .from(userPlaceLogMapping)
                .where(userPlaceLogMapping.user.id.eq(userId))
                .where(userPlaceLogMapping.placeLog.id.eq(placeLogId))
                .fetchFirst();

        return result != null;
    }
}
