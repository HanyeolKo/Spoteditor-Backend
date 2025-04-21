package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.follow.controller.dto.FollowRequest;
import com.spoteditor.backend.modules.follow.controller.dto.FollowResponse;
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

public interface FollowApiDocument {

	@Operation(summary = "팔로우 추가", description = "특정 사용자를 팔로우합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "201",
					description = "팔로우 성공",
					content = @Content(schema = @Schema(implementation = Void.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청 또는 중복된 팔로우",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "BAD_REQUEST",
                        "code": "F001",
                        "message": "중복 팔로우는 할 수 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "UNAUTHORIZED",
                        "code": "U003",
                        "message": "현재 사용자의 권한이 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "NOT_FOUND",
                        "code": "U002",
                        "message": "해당 유저를 찾을 수 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			)
	})
	ResponseEntity<Void> follow(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "팔로우할 사용자 정보", required = true, schema = @Schema(implementation = FollowRequest.class))
			FollowRequest request
	);

	@Operation(summary = "팔로우 취소", description = "특정 사용자에 대한 팔로우를 취소합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "204",
					description = "팔로우 취소 성공",
					content = @Content(schema = @Schema(implementation = Void.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "BAD_REQUEST",
                        "code": "C002",
                        "message": "invalid input type",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "UNAUTHORIZED",
                        "code": "U003",
                        "message": "현재 사용자의 권한이 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "NOT_FOUND",
                        "code": "U002",
                        "message": "해당 유저를 찾을 수 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			)
	})
	ResponseEntity<Void> unfollow(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "언팔로우할 사용자 정보", required = true, schema = @Schema(implementation = FollowRequest.class))
			FollowRequest request
	);

	@Operation(summary = "팔로잉 목록 조회", description = "현재 사용자가 팔로우하고 있는 사용자 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "팔로잉 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = CustomPageResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "UNAUTHORIZED",
                        "code": "U003",
                        "message": "현재 사용자의 권한이 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "NOT_FOUND",
                        "code": "U002",
                        "message": "해당 유저를 찾을 수 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			)
	})
	ResponseEntity<CustomPageResponse<FollowResponse>> followingList(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "페이지 요청 정보", required = true) CustomPageRequest request
	);

	@Operation(summary = "팔로워 목록 조회", description = "현재 사용자를 팔로우하고 있는 사용자 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "팔로워 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = CustomPageResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "UNAUTHORIZED",
                        "code": "U003",
                        "message": "현재 사용자의 권한이 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "존재하지 않는 사용자",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                    {
                        "status": "NOT_FOUND",
                        "code": "U002",
                        "message": "해당 유저를 찾을 수 없습니다.",
                        "timestamp": "2025-02-18T07:49:53.0322"
                    }
                    """
							)
					)
			)
	})
	ResponseEntity<CustomPageResponse<FollowResponse>> followerList(
			@Parameter(description = "인증된 사용자 정보", required = true) UserIdDto dto,
			@Parameter(description = "페이지 요청 정보", required = true) CustomPageRequest request
	);
}