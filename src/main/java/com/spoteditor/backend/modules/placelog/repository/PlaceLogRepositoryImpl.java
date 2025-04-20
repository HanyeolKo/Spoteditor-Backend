package com.spoteditor.backend.modules.placelog.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.entity.PlaceLogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.image.entity.QPlaceImage.placeImage;
import static com.spoteditor.backend.modules.mapping.userplacelogmapping.entity.QUserPlaceLogMapping.userPlaceLogMapping;
import static com.spoteditor.backend.modules.placelog.entity.QPlaceLog.placeLog;

@Repository
@RequiredArgsConstructor
public class PlaceLogRepositoryImpl implements PlaceLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public CustomPageResponse<PlaceLogListResponse> findAllPlace(CustomPageRequest request) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                            placeImage.id,
                            placeImage.originalFile,
                            placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(placeLog.count())
                .from(placeLog);

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }

    @Override
    public CustomPageResponse<PlaceLogListResponse> findMyPlaceLog(Long userId, CustomPageRequest request) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                                placeImage.id,
                                placeImage.originalFile,
                                placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(placeLog.user.id.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(placeLog.count())
                .from(placeLog)
                .where(placeLog.user.id.eq(userId));

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }

    @Override
    public CustomPageResponse<PlaceLogListResponse> findOtherPlaceLog(Long userId, CustomPageRequest request) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                                placeImage.id,
                                placeImage.originalFile,
                                placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(placeLog.user.id.eq(userId))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(placeLog.count())
                .from(placeLog)
                .where(placeLog.user.id.eq(userId))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC));

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }

    @Override
    public CustomPageResponse<PlaceLogListResponse> findMyBookmarkPlaceLog(Long userId, CustomPageRequest request) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                                placeImage.id,
                                placeImage.originalFile,
                                placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(userPlaceLogMapping)
                .join(userPlaceLogMapping.placeLog, placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(userPlaceLogMapping.user.id.eq(userId))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(userPlaceLogMapping.count())
                .from(userPlaceLogMapping)
                .where(userPlaceLogMapping.user.id.eq(userId));

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }

    @Override
    public CustomPageResponse<PlaceLogListResponse> searchBySidoBname(CustomPageRequest request, String sido, String bname) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                                placeImage.id,
                                placeImage.originalFile,
                                placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(placeLog.address.sido.eq(sido))
                .where(placeLog.address.bname.eq(bname))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(placeLog.count())
                .from(placeLog)
                .where(placeLog.address.sido.eq(sido))
                .where(placeLog.address.bname.eq(bname))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC));

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }

    @Override
    public CustomPageResponse<PlaceLogListResponse> searchByName(CustomPageRequest request, String name) {
        PageRequest pageRequest = request.of();

        List<PlaceLogListResponse> placeLogList = queryFactory
                .select(Projections.constructor(PlaceLogListResponse.class,
                        placeLog.id,
                        placeLog.user.name,
                        placeLog.name,
                        Projections.constructor(PlaceImageResponse.class,
                                placeImage.id,
                                placeImage.originalFile,
                                placeImage.storedFile
                        ),
                        placeLog.address,
                        placeLog.views
                ))
                .from(placeLog)
                .leftJoin(placeLog.placeLogImage, placeImage)
                .where(placeLog.name.contains(name))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .orderBy(placeLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> queryCount = queryFactory
                .select(placeLog.count())
                .from(placeLog)
                .where(placeLog.name.contains(name))
                .where(placeLog.status.eq(PlaceLogStatus.PUBLIC));

        Page<PlaceLogListResponse> page = PageableExecutionUtils.getPage(
                placeLogList,
                pageRequest,
                queryCount::fetchOne
        );

        return new CustomPageResponse<>(page);
    }
}
