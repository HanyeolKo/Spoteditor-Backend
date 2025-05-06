package com.spoteditor.backend.modules.placelog.service;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogSortType;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogPopularityRedisRepository;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/*
        현재 : redis 인기순 목록 조회 -> DB조회시 인기순 목록 참조하여 정렬
        
        리팩터링시 : DB 검색결과 조회 -> 조회한 ID값들만 Redis 인기순 목록에서 조회 -> 가져온 Redis 인기순 목록을 참조하여 재정렬 방법으로 리팩터링이 필요할 수도있음
        (Redis 버전에따라 batch를 돌려야 할수도있음)
        2안 : redis 저장시 게시물 제목과 주소(시,구) 같이 저장하여 Redis에서 조건 검색 선행 - 불가능할 가능성이 높음

*/

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

        CustomPageResponse<PlaceLogListResponse> response;

        if(sortType.equals(PlaceLogSortType.POPULARITY)){       //인기순 정렬
            List<PlaceLogListResponse> searchDataAfterSort = placeLogReOrderPopularity(placeLogRepository.searchAllBySidoBname(sido, gugun));

            List<PlaceLogListResponse> contents = pagingOnSearchData(pageRequest, searchDataAfterSort);

            Page<PlaceLogListResponse> p = PageableExecutionUtils.getPage(
                    contents,
                    pageRequest.of(),
                    searchDataAfterSort::size
            );

            return new CustomPageResponse<>(p);
        }else{          //그외 (최신순)
            response = placeLogRepository.searchBySidoBname(pageRequest, sido, gugun);
        }

        return response;
    }

    /**
     * 이름 검색
     * @return
     */
    public CustomPageResponse<PlaceLogListResponse> searchPlaceLogAtName(String keyWord, PlaceLogSortType sortType, CustomPageRequest pageRequest){

        CustomPageResponse<PlaceLogListResponse> response;

        if(sortType.equals(PlaceLogSortType.POPULARITY)){
            List<PlaceLogListResponse> searchDataAfterSort = placeLogReOrderPopularity(placeLogRepository.searchAllByName(keyWord));

            List<PlaceLogListResponse> contents = pagingOnSearchData(pageRequest, searchDataAfterSort);

            Page<PlaceLogListResponse> p = PageableExecutionUtils.getPage(
                    contents,
                    pageRequest.of(),
                    searchDataAfterSort::size
            );

            return new CustomPageResponse<>(p);
        }else{
            response = placeLogRepository.searchByName(pageRequest, keyWord);
        }

        return response;
    }

    /**
     * 전체 Place Log중 인기도순서로 파라미터 갯수만큼 반환
     * @return
     */
    public List<PlaceLogListResponse> popularityPlaceLog(int top) {

        // 1. Redis Sorted Set에서 상위 N개의 ID를 읽어옴
        List<Long> topIds = placeLogPopularityRedisRepository.getPopularityList(top);

        // 2. DB에서 해당 ID 들을 조회 (redis에서 가져온 순서 유지)
        List<PlaceLogListResponse> searchData = placeLogRepository.findByIdInPreserveOrder(topIds);

        return searchData;
    }

    /**
     * 전체 Place Log중 인기도순으로 조회(페이징) 
     * @return
     */
    public CustomPageResponse<PlaceLogListResponse> popularityPagingPlaceLog(CustomPageRequest pageRequest) {

        // 1. Redis Sorted Set에서 전체 게시글의 ID를 읽어옴
        List<Long> topIds = placeLogPopularityRedisRepository.getAllPopularityList();

        // 2. DB에서 해당 ID 들을 조회 (redis에서 가져온 순서 유지)
        List<PlaceLogListResponse> searchData = placeLogRepository.findByIdInPreserveOrder(topIds);

        List<PlaceLogListResponse> contents = pagingOnSearchData(pageRequest, searchData);

        Page<PlaceLogListResponse> p = PageableExecutionUtils.getPage(
                contents,
                pageRequest.of(),
                searchData::size
        );

        return new CustomPageResponse<>(p);
    }

    /**
     * Redis에서 게시글의 전체 인기순위 리스트를 가져와 재정렬 하여 return
     * @param searchDataOrderByRecent   search 필터링된 데이터 리스트
     * @return
     */
    public List<PlaceLogListResponse> placeLogReOrderPopularity(List<PlaceLogListResponse> searchDataOrderByRecent){
        List<Long> popularityList = placeLogPopularityRedisRepository.getAllPopularityList();

        Map<Long, PlaceLogListResponse> placeLogMap = searchDataOrderByRecent.stream()
                .collect(Collectors.toMap(PlaceLogListResponse::placeLogId, Function.identity()));

        return popularityList.stream()
                .map(placeLogMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 페이징 처리
     */
    public List<PlaceLogListResponse> pagingOnSearchData(CustomPageRequest request, List<PlaceLogListResponse> searchDataAfterSort) {

        PageRequest pageRequest = request.of();

        long offset = pageRequest.getOffset();              // 시작 인덱스
        int limit = pageRequest.getPageSize();              // 페이지당 게시글수
        int toIndex = (int) Math.min(offset + limit, searchDataAfterSort.size());       // 마지막 게시글의 인덱스

        // 페이징 처리
        List<PlaceLogListResponse> pageContent = searchDataAfterSort.subList(
                (int) Math.min(offset, searchDataAfterSort.size()),
                toIndex
        );

        return pageContent;
    }

}
