package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogListResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogRegisterRequest;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogResponse;
import com.spoteditor.backend.modules.placelog.controller.dto.PlaceLogUpdateRequest;
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

public interface PlaceLogApiDocument {

    @Operation(
            summary = "로그 저장",
            description = "로그 저장",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "CREATED",
                    content = @Content(schema = @Schema(implementation = PlaceLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "등록하려는 이미지를 찾을 수 없을 때",
                                            value = """
                                                    {
                                                        "status": "NOT_FOUND_IMAGE",
                                                        "code": "I001",
                                                        "message": "이미지를 찾을 수 없습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
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
                                            name = "삭제된 유저일 때",
                                            value = """
                                                    {
                                                        "status": "DELETED_USER",
                                                        "code": "U004",
                                                        "message": "삭제된 유저입니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 제목이 없을 때",
                                            value = """
                                                    {
                                                        "status": "NO_PLACE_LOG_NAME",
                                                        "code": "PL009",
                                                        "message": "저장하려면 제목이 필요합니다",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 장소가 등록되지 않았을 때",
                                            value = """
                                                    {
                                                        "status": "PLACE_MINIMUM_REQUIRED",
                                                        "code": "PL002",
                                                        "message": "로그에 장소는 최소 1개 이상 등록해야 합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 장소가 10개 초과로 등록되었을 때",
                                            value = """
                                                    {
                                                        "status": "PLACE_LIMIT_EXCEEDED",
                                                        "code": "PL003",
                                                        "message": "로그에 장소는 최대 10개까지 등록 가능합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 등록하는 사진 개수와 UUID 개수가 일치하지 않을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_UUID_MISMATCH",
                                                        "code": "I002",
                                                        "message": "사진 개수와 UUID 개수가 일치해야 합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 사진을 등록하지 않았을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_MINIMUM_REQUIRED",
                                                        "code": "P002",
                                                        "message": "장소에 이미지를 최소 1장 이상 등록해야합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 사진을 3장 초과로 등록했을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_LIMIT_EXCEEDED",
                                                        "code": "P003",
                                                        "message": "장소에 이미지를 최대 3장까지 등록 가능합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<PlaceLogResponse> savePlaceLog(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @Parameter(
                    description = "등록하려는 로그 정보",
                    required = true
            ) PlaceLogRegisterRequest request
    );

    @Operation(
            summary = "로그 리스트 가져오기",
            description = "로그 리스트 가져오기",
            method = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = CustomPageResponse.class))
            )
    })
    ResponseEntity<CustomPageResponse<PlaceLogListResponse>> getPlaceLogs(
            @Parameter(
                    description = "페이지 요청 정보 (page, size)",
                    required = true
            ) CustomPageRequest pageRequest
    );

    @Operation(
            summary = "단일 로그 가져오기",
            description = "단일 로그 가져오기",
            method = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = PlaceLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 Place Log가 없을 때",
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
                    responseCode = "403",
                    description = "FORBIDDEN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 로그가 비공개 로그일 때",
                                            value = """
                                                    {
                                                        "status": "PRIVATE_PLACE_LOG",
                                                        "code": "PL011",
                                                        "message": "비공개 로그는 소유자만 조회 가능합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<PlaceLogResponse> getPlaceLog(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @PathVariable Long placeLogId
    );

    @Operation(
            summary = "단일 로그 수정",
            description = "단일 로그 수정",
            method = "PATCH"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(schema = @Schema(implementation = PlaceLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "FORBIDDEN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "로그 소유자가 아닐 때",
                                            value = """
                                                    {
                                                        "status": "NOT_PLACE_LOG_OWNER",
                                                        "code": "PL006",
                                                        "message": "로그 소유자가 아닙니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
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
                                            name = "삭제된 유저일 때",
                                            value = """
                                                    {
                                                        "status": "DELETED_USER",
                                                        "code": "U004",
                                                        "message": "삭제된 유저입니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 제목이 없을 때",
                                            value = """
                                                    {
                                                        "status": "NO_PLACE_LOG_NAME",
                                                        "code": "PL009",
                                                        "message": "저장하려면 제목이 필요합니다",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 장소가 등록되지 않았을 때",
                                            value = """
                                                    {
                                                        "status": "PLACE_MINIMUM_REQUIRED",
                                                        "code": "PL002",
                                                        "message": "로그에 장소는 최소 1개 이상 등록해야 합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "로그에 장소가 10개 초과로 등록되었을 때",
                                            value = """
                                                    {
                                                        "status": "PLACE_LIMIT_EXCEEDED",
                                                        "code": "PL003",
                                                        "message": "로그에 장소는 최대 10개까지 등록 가능합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 등록하는 사진 개수와 UUID 개수가 일치하지 않을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_UUID_MISMATCH",
                                                        "code": "I002",
                                                        "message": "사진 개수와 UUID 개수가 일치해야 합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 사진을 등록하지 않았을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_MINIMUM_REQUIRED",
                                                        "code": "P002",
                                                        "message": "장소에 이미지를 최소 1장 이상 등록해야합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "장소에 사진을 3장 초과로 등록했을 때",
                                            value = """
                                                    {
                                                        "status": "IMAGE_LIMIT_EXCEEDED",
                                                        "code": "P003",
                                                        "message": "장소에 이미지를 최대 3장까지 등록 가능합니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "수정하려는 장소의 id가 없을 때",
                                            value = """
                                                    {
                                                        "status": "NOT_FOUND_PLACE",
                                                        "code": "P001",
                                                        "message": "해당 장소를 찾을 수 없습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "수정하려는 장소가 로그안에 없을 때",
                                            value = """
                                                    {
                                                        "status": "NOT_FOUND_PLACES",
                                                        "code": "PL001",
                                                        "message": "로그에 등록된 장소들을 찾을 수 없습니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<PlaceLogResponse> updatePlaceLog(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @PathVariable Long placeLogId,
            @Parameter(
                    description = "수정하려는 로그 정보",
                    required = true
            ) PlaceLogUpdateRequest request
    );


    @Operation(
            summary = "단일 로그 삭제",
            description = "단일 로그 삭제",
            method = "DELETE"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "NO_CONTENT"
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
                    responseCode = "403",
                    description = "FORBIDDEN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "해당 로그의 소유자가 아닐 때",
                                            value = """
                                                    {
                                                        "status": "NOT_PLACE_LOG_OWNER",
                                                        "code": "PL006",
                                                        "message": "로그 소유자가 아닙니다.",
                                                        "timestamp": "2025-02-17T16:50:36.569347"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<Void> removePlaceLog(
            @Parameter(
                    description = "인증된 사용자 정보",
                    required = true
            ) UserIdDto userIdDto,
            @PathVariable Long placeLogId
    );
}
