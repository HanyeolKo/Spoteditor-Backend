package com.spoteditor.backend.modules.placelog.controller;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.global.exception.PlaceLogException;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogSortType;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spoteditor.backend.global.response.ErrorCode.INVALID_TYPE_VALUE;
import static com.spoteditor.backend.modules.placelog.entity.QPlaceLog.placeLog;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceLogSearchController {

    private final PlaceLogRepository placeLogRepository;

    @GetMapping("/search/placelogs/address")
    public ResponseEntity<CustomPageResponse<?>> getPlaceLogsByAddress(
            @ModelAttribute CustomPageRequest pageRequest,
            @RequestParam String sido,
            @RequestParam String bname,
            @RequestParam(defaultValue = "RECENT") PlaceLogSortType sort
            ) {

        pageRequest.setSortProperty(sort.getColumnName());

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchBySidoBname(pageRequest, sido, bname, sort);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/search/placelogs/name")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByName(
            @ModelAttribute CustomPageRequest pageRequest,
            @RequestParam String name,
            @RequestParam(defaultValue = "RECENT") PlaceLogSortType sort
    ) {
        String searchName = name.trim();
        if(searchName.length() < 2) {
            throw new PlaceLogException(INVALID_TYPE_VALUE);
        }

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchByName(pageRequest, searchName, sort);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/placelogs/popularity")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByPopularity(
            @ModelAttribute CustomPageRequest request
    ){
        // 인기도순 강제정렬
        // 서비스 로직으로 분리하는게 명확하긴한데...
        PlaceLogSortType sortType = PlaceLogSortType.POPULARITY;
        request.setSortProperty(sortType.getColumnName());

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.findAllPlace(request, sortType.getOrderSpecifier(request.getDirection()));

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
