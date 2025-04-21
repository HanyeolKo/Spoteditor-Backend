package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface PlaceLogBookmarkApiDocument {
    @Operation(
            summary = "로그 북마크",
            description = "로그 북마크",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 로그가 없을 때",
                                            value = """
                                                    {
                                                        "status": "NOT_FOUND_PLACE_LOG",
                                                        "code": "PL005",
                                                        "message": "로그를 찾을 수 없습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CONFLICT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 해당 로그와의 북마크가 존재할 때",
                                            value = """
                                                    {
                                                        "status": "BOOKMARK_ALREADY_EXIST",
                                                        "code": "B003",
                                                        "message": "북마크가 이미 존재합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<Void> addBookmark(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @PathVariable Long placeLogId
    );

    @Operation(
            summary = "로그 북마크 삭제",
            description = "로그 북마크 삭제",
            method = "DELETE"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "NO_CONTENT",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 로그가 없을 때",
                                            value = """
                                                    {
                                                        "status": "NOT_FOUND_PLACE_LOG",
                                                        "code": "PL005",
                                                        "message": "로그를 찾을 수 없습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "CONFLICT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 로그와의 북마크가 존재하지 않을 때",
                                            value = """
                                                    {
                                                        "status": "BOOKMARK_ALREADY_REMOVED",
                                                        "code": "B004",
                                                        "message": "북마크가 존재하지 않습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<Void> removeBookmark(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @PathVariable Long placeLogId
    );
}
