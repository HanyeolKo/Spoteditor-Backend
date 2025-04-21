package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.modules.bookmark.controller.dto.BookmarkRequest;
import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface BookmarkApiDocument {

	@Operation(summary = "북마크 추가", description = "장소에 대한 북마크를 추가합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "201",
					description = "북마크 추가 성공",
					content = @Content(schema = @Schema(implementation = Void.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "유효하지 않은 입력 값",
											value = """
                        {
                            "status": "BAD_REQUEST",
                            "code": "C004",
                            "message": "invalid type value",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											value = """
                        {
                            "status": "UNAUTHORIZED",
                            "code": "U003",
                            "message": "현재 사용자의 권한이 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 장소",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											value = """
                        {
                            "status": "NOT_FOUND",
                            "code": "P001",
                            "message": "해당 장소를 찾을 수 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "409",
					description = "북마크 처리 실패",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "북마크 처리 실패",
											value = """
                        {
                            "status": "CONFLICT",
                            "code": "B002",
                            "message": "북마크 처리에 실패했습니다. 잠시 후 다시 시도해주세요.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									),
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "북마크 중복",
											value = """
                        {
                            "status": "CONFLICT",
                            "code": "B003",
                            "message": "북마크가 이미 존재합니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			)
	})
	ResponseEntity<Void> addBookmark(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "북마크 추가 요청 정보", required = true, schema = @Schema(implementation = BookmarkRequest.class))
			BookmarkRequest bookmarkRequest
	) throws InterruptedException;

	@Operation(summary = "북마크 삭제", description = "장소에 대한 북마크를 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "204",
					description = "북마크 삭제 성공",
					content = @Content(schema = @Schema(implementation = Void.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "유효하지 않은 입력 값",
											value = """
                        {
                            "status": "BAD_REQUEST",
                            "code": "C004",
                            "message": "invalid type value",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											value = """
                        {
                            "status": "UNAUTHORIZED",
                            "code": "U003",
                            "message": "현재 사용자의 권한이 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 북마크",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											value = """
                        {
                            "status": "NOT_FOUND",
                            "code": "B001",
                            "message": "해당 북마크를 찾을 수 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "409",
					description = "북마크 처리 실패",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "북마크 처리 실패",
											value = """
                        {
                            "status": "CONFLICT",
                            "code": "B002",
                            "message": "북마크 처리에 실패했습니다. 잠시 후 다시 시도해주세요.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									),
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											name = "북마크 존재하지 않음",
											value = """
                        {
                            "status": "CONFLICT",
                            "code": "B004",
                            "message": "북마크가 존재하지 않습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			)
	})
	ResponseEntity<Void> removeBookmark(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "북마크 삭제 요청 정보", required = true, schema = @Schema(implementation = BookmarkRequest.class))
			BookmarkRequest bookmarkRequest
	) throws InterruptedException;
}