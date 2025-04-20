package com.spoteditor.backend.modules.placelog.controller;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.config.swagger.docs.PlaceLogApiDocument;
import com.spoteditor.backend.modules.follow.repository.FollowRepository;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogRegisterRequest;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogUpdateRequest;
import com.spoteditor.backend.modules.placelog.repository.PlaceLogRepository;
import com.spoteditor.backend.modules.placelog.service.PlaceLogService;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogRegisterCommand;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogResult;
import com.spoteditor.backend.modules.placelog.service.dto.PlaceLogUpdateCommand;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceLogController implements PlaceLogApiDocument {

    private final PlaceLogService placeLogService;
    private final PlaceLogRepository placeLogRepository;
    private final FollowRepository followRepository;

    @Override
    @PostMapping("/placelogs")
    public ResponseEntity<PlaceLogResponse> savePlaceLog(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @RequestBody PlaceLogRegisterRequest request
    ) {
        PlaceLogRegisterCommand command = PlaceLogRegisterCommand.from(request);
        PlaceLogResult result = placeLogService.addPlaceLog(userIdDto.getId(), command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PlaceLogResponse.from(result));
    }

    @Override
    @GetMapping("/placelogs")
    public ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogs(
            CustomPageRequest pageRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(placeLogRepository.findAllPlace(pageRequest));
    }

    @Override
    @GetMapping("/placelogs/{placeLogId}")
    public ResponseEntity<PlaceLogResponse> getPlaceLog(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId
    ) {
        PlaceLogResult result;
        boolean isFollowing;

        if (userIdDto == null) {
            result = placeLogService.getPublicPlaceLog(placeLogId);
            isFollowing = false;
        } else {
            result = placeLogService.getPlaceLog(userIdDto.getId(), placeLogId);
            isFollowing = followRepository.findIsFollowing(userIdDto.getId(), result.placeLog().getUser().getId());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PlaceLogResponse.from(result, isFollowing));
    }

    @Override
    @PatchMapping("/placelogs/{placeLogId}")
    public ResponseEntity<PlaceLogResponse> updatePlaceLog(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId,
            @RequestBody PlaceLogUpdateRequest request
    ) {
        PlaceLogUpdateCommand command = PlaceLogUpdateCommand.from(request);
        PlaceLogResult result = placeLogService.updatePlaceLog(userIdDto.getId(), placeLogId, command);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PlaceLogResponse.from(result));
    }

    @Override
    @DeleteMapping("/placelogs/{placeLogId}")
    public ResponseEntity<Void> removePlaceLog(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId
    ) {
        placeLogService.removePlaceLog(userIdDto.getId(), placeLogId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}