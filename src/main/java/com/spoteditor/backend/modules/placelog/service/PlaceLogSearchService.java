package com.spoteditor.backend.modules.placelog.service;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogSortType;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogPopularityRedisRepository;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class PlaceLogSearchService {

    private final PlaceLogRepository placeLogRepository;
    private final PlaceLogPopularityRedisRepository placeLogPopularityRedisRepository;

    /**
     * 주소 검색
     * @return
     */
    public CustomPageResponse<PlaceLogListResponse> searchPlaceLogAtAddress(String sido, String gugun, PlaceLogSortType sortType, CustomPageRequest pageRequest) {

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchBySidoBname(pageRequest, sido, gugun);

        if(sortType.equals(PlaceLogSortType.POPULARITY)){
            List<PlaceLogListResponse> contents = placeLogReOrderPopularity(response);

            Page<PlaceLogListResponse> p = PageableExecutionUtils.getPage(
                    contents,
                    pageRequest.of(),
                    contents::size
            );

            return new CustomPageResponse<>(p);
        }

        return response;
    }

    /**
     * 이름 검색
     * @return
     */
    public CustomPageResponse<PlaceLogListResponse> searchPlaceLogAtName(String keyWord, PlaceLogSortType sortType, CustomPageRequest pageRequest){

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchByName(pageRequest, keyWord);

        if(sortType.equals(PlaceLogSortType.POPULARITY)){
            List<PlaceLogListResponse> contents = placeLogReOrderPopularity(response);

            Page<PlaceLogListResponse> p = PageableExecutionUtils.getPage(
                    contents,
                    pageRequest.of(),
                    contents::size
            );

            return new CustomPageResponse<>(p);
        }

        return response;
    }

    /**
     * 전체 Place Log중 인기도가 가장 높은 게시물 순서로 조회
     * @param top 최상위부터 조회할 게시물 갯수
     * @return
     */
    public List<PlaceLogListResponse> popularityPlaceLog(int top){

        // 1. Redis Sorted Set에서 상위 N개의 ID를 읽어옴
        List<Long> topIds = placeLogPopularityRedisRepository.getPopularityList(top);

        // 2. DB에서 해당 ID 들을 조회 (redis에서 가져온 순서 유지)
        List<PlaceLogListResponse> placeLogs = placeLogRepository.findByIdInPreserveOrder(topIds);

        return placeLogs;
    }

    //지금 만들어 놓은 Repository Return 구조가 CustomPageResponse라 일단 그대로 사용(리팩터링 여지 있음)
    /**
     * Redis에서 게시글의 전체 인기순위 리스트를 가져와 재정렬 하여 return
     * @param searchDataOrderByRecent   search 필터링된 데이터 리스트
     * @return
     */
    public List<PlaceLogListResponse> placeLogReOrderPopularity(CustomPageResponse<PlaceLogListResponse> searchDataOrderByRecent){
        List<Long> popularityList = placeLogPopularityRedisRepository.getAllPopularityList();

        List<PlaceLogListResponse> placeLogList = searchDataOrderByRecent.getContent();

        Map<Long, PlaceLogListResponse> placeLogMap = placeLogList.stream()
                .collect(Collectors.toMap(PlaceLogListResponse::placeLogId, Function.identity()));

        return popularityList.stream()
                .map(placeLogMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

}
