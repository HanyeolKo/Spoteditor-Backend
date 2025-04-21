package com.spoteditor.backend.modules.place.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.image.entity.QPlaceImage;
import com.spoteditor.backend.modules.place.controller.dto.PlaceResponse;
import com.spoteditor.backend.modules.place.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoteditor.backend.modules.bookmark.entity.QBookmark.bookmark;
import static com.spoteditor.backend.modules.image.entity.QPlaceImage.placeImage;
import static com.spoteditor.backend.modules.place.entity.QPlace.place;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CustomPageResponse<PlaceResponse> findAllPlace(CustomPageRequest request) {
		PageRequest pageRequest = request.of();

		QPlaceImage subPlaceImage = new QPlaceImage("subPlaceImage");

		List<PlaceResponse> placeList = queryFactory
				.select(Projections.constructor(PlaceResponse.class,
						place.id.as("placeId"),
						place.user.name.as("author"),
						place.name,
						place.description,
						place.address,
						place.category,
						Projections.constructor(PlaceImageResponse.class,
								placeImage.id,
								placeImage.originalFile,
								placeImage.storedFile
						)
				))
				.from(place)
				.leftJoin(placeImage).on(placeImage.place.id.eq(place.id)
						.and(placeImage.createdAt.eq(
								JPAExpressions
										.select(subPlaceImage.createdAt.min())
										.from(subPlaceImage)
										.where(subPlaceImage.place.id.eq(place.id))
						)
				))
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(place.createdAt.asc())
				.fetch();

		JPAQuery<Long> queryCount = queryFactory
				.select(place.count())
				.from(place);

		Page<PlaceResponse> page = PageableExecutionUtils.getPage(
				placeList,
				pageRequest,
				queryCount::fetchOne
		);

		return new CustomPageResponse<>(page);
	}

	@Override
	public CustomPageResponse<PlaceResponse> findMyBookmarkPlace(Long userId, CustomPageRequest request) {
		PageRequest pageRequest = request.of();

		QPlaceImage subPlaceImage = new QPlaceImage("subPlaceImage");

		List<PlaceResponse> placeList = queryFactory
				.select(Projections.constructor(PlaceResponse.class,
						place.id.as("placeId"),
						place.user.name.as("author"),
						place.name,
						place.description,
						place.address,
						place.category,
						Projections.constructor(PlaceImageResponse.class,
								placeImage.id,
								placeImage.originalFile,
								placeImage.storedFile
						)
				))
				.from(bookmark)
				.join(bookmark.place, place)
				.leftJoin(placeImage).on(placeImage.place.id.eq(place.id)
						.and(placeImage.createdAt.eq(
								JPAExpressions
										.select(subPlaceImage.createdAt.min())
										.from(subPlaceImage)
										.where(subPlaceImage.place.id.eq(place.id))
						)
				))
				.where(bookmark.user.id.eq(userId))
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(place.createdAt.desc())
				.fetch();

		JPAQuery<Long> queryCount = queryFactory
				.select(bookmark.count())
				.from(bookmark)
				.where(bookmark.user.id.eq(userId));

		Page<PlaceResponse> page = PageableExecutionUtils.getPage(
				placeList,
				pageRequest,
				queryCount::fetchOne
		);

		return new CustomPageResponse<>(page);
	}

	@Override
	public List<Place> findByIdIn(List<Long> placeIds) {
		return queryFactory
				.selectFrom(place)
				.where(place.id.in(placeIds))
				.fetch();
	}
  

	@Override
	public List<Place> findAllPlacesByUserId(Long userId) {
		return queryFactory
				.selectFrom(place)
				.leftJoin(place.placeImages, placeImage).fetchJoin()
				.where(place.user.id.eq(userId))
				.distinct()
				.fetch();
	}
}
