package com.spoteditor.backend.modules.placelog.service.dto;

import java.time.LocalDateTime;

public record PlaceLogWithBookmark(
    Long placeLogId,
    Long viewCount,
    LocalDateTime createAt,
    Long bookmarkCount
){}
