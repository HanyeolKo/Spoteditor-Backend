package com.spoteditor.backend.modules.placelog.controller;

import com.spoteditor.backend.modules.bookmark.controller.dto.BookmarkResponse;
import com.spoteditor.backend.config.swagger.docs.PlaceLogBookmarkApiDocument;
import com.spoteditor.backend.modules.mapping.userplacelogmapping.repository.UserPlaceLogMappingRepository;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogBookmarkResponse;
import com.spoteditor.backend.modules.placelog.service.PlaceLogBookmarkService;
import com.spoteditor.backend.modules.placelog.service.PlaceLogService;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlaceLogBookmarkController implements PlaceLogBookmarkApiDocument {

    private final PlaceLogBookmarkService placeLogBookmarkService;
    private final PlaceLogService placeLogService;
    private final UserPlaceLogMappingRepository userPlaceLogMappingRepository;

    @Override
    @PostMapping("/placelogs/{placeLogId}/bookmark")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId
    ) {
        placeLogBookmarkService.addBookmark(userIdDto.getId(), placeLogId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    @DeleteMapping("/placelogs/{placeLogId}/bookmark")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId
    ) {
        placeLogBookmarkService.removeBookmark(userIdDto.getId(), placeLogId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/placelogs/{placeLogId}/bookmark")
    public ResponseEntity<List<PlaceLogBookmarkResponse>> getBookmarkStatus(
            @AuthenticationPrincipal UserIdDto userIdDto,
            @PathVariable Long placeLogId
    ) {
        List<PlaceLogBookmarkResponse> placeBookmarkStatus = placeLogService.getPlaceBookmarkStatus(userIdDto.getId(), placeLogId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(placeBookmarkStatus);
    }

    @GetMapping("/placelogs/bookmark/check")
    public ResponseEntity<BookmarkResponse> isBookmarked(
            @AuthenticationPrincipal UserIdDto dto,
            @RequestParam Long placeLogId
    ) {
        boolean isBookmarked = userPlaceLogMappingRepository.existsByUserIdAndPlaceLogId(dto.getId(), placeLogId);
        BookmarkResponse response = new BookmarkResponse(isBookmarked);

        return ResponseEntity.ok(response);
    }
}
