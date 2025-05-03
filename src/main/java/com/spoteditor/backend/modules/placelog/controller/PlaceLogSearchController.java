package com.spoteditor.backend.modules.placelog.controller;

import com.spoteditor.backend.global.exception.PlaceLogException;
import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogSortType;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.PlaceLogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.spoteditor.backend.global.response.ErrorCode.INVALID_TYPE_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceLogSearchController {

    private final PlaceLogRepository placeLogRepository;
    private final PlaceLogSearchService placeLogSearchService;

    @GetMapping("/search/placelogs/address")
    public ResponseEntity<CustomPageResponse<?>> getPlaceLogsByAddress(
            @RequestParam CustomPageRequest pageRequest,
            @RequestParam String sido,
            @RequestParam String bname,
            @RequestParam(defaultValue = "RECENT") PlaceLogSortType sort
            ) {

        CustomPageResponse<PlaceLogListResponse> response = placeLogSearchService.searchPlaceLogAtAddress(sido, bname, sort, pageRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/search/placelogs/name")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByName(
            @RequestParam CustomPageRequest pageRequest,
            @RequestParam String name,
            @RequestParam(defaultValue = "RECENT") PlaceLogSortType sort
    ) {
        String searchName = name.trim();
        if(searchName.length() < 2) {
            throw new PlaceLogException(INVALID_TYPE_VALUE);
        }

        CustomPageResponse<PlaceLogListResponse> response = placeLogSearchService.searchPlaceLogAtName(name, sort, pageRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/placelogs/popularity")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByPagingPopularity(
            @RequestParam CustomPageRequest pageRequest
    ) {
        CustomPageResponse<PlaceLogListResponse> response = placeLogSearchService.popularityPagingPlaceLog(pageRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
