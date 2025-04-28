package com.spoteditor.backend.modules.placelog.controller;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.global.exception.PlaceLogException;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import lombok.RequiredArgsConstructor;
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
            @RequestParam String bname
    ) {
        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchBySidoBname(pageRequest, sido, bname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/search/placelogs/name")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByName(
            CustomPageRequest pageRequest,
            @RequestParam String name
    ) {
        String searchName = name.trim();
        if(searchName.length() < 2) {
            throw new PlaceLogException(INVALID_TYPE_VALUE);
        }

        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.searchByName(pageRequest, searchName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/search/placelogs/popularity")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogsByPopularity(CustomPageRequest request){
        CustomPageResponse<PlaceLogListResponse> response = placeLogRepository.findAllPlace(request, placeLog.popularityScore.desc());

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
