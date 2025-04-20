package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.place.controller.dto.PlaceRegisterRequest;
import com.spoteditor.backend.modules.place.controller.dto.PlaceRegisterResponse;
import com.spoteditor.backend.modules.place.controller.dto.PlaceResponse;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "place", description = "장소 API")
public interface PlaceApiDocument {

	@Operation(
			summary = "장소 등록",
			description = "새로운 장소를 등록합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "장소 등록 성공",
					content = @Content(schema = @Schema(implementation = PlaceRegisterResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@ExampleObject(
											name = "유효하지 않은 입력 값",
											value = """
                                    {
                                        "status": "BAD_REQUEST",
                                        "code": "C004",
                                        "message": "invalid type value",
                                        "timestamp": "2024-02-18T14:30:00.000000"
                                    }
                                    """
									),
									@ExampleObject(
											name = "잘못된 인증 정보",
											value = """
                                    {
                                        "status": "BAD_REQUEST",
                                        "code": "C005",
                                        "message": "bad credentials",
                                        "timestamp": "2024-02-18T14:30:00.000000"
                                    }
                                    """
									)
							}
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증 실패",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                                    {
                                        "status": "UNAUTHORIZED",
                                        "code": "T001",
                                        "message": "토큰 만료",
                                        "timestamp": "2024-02-18T14:30:00.000000"
                                    }
                                    """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "장소를 찾을 수 없음",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                                    {
                                        "status": "NOT_FOUND",
                                        "code": "P001",
                                        "message": "해당 장소를 찾을 수 없습니다.",
                                        "timestamp": "2024-02-18T14:30:00.000000"
                                    }
                                    """
							)
					)
			)
	})
	ResponseEntity<PlaceRegisterResponse> addPlace(
			@Parameter(description = "인증된 사용자 정보", hidden = true)
			@AuthenticationPrincipal UserIdDto dto,

			@Parameter(
					description = "장소 등록 정보",
					required = true,
					content = @Content(schema = @Schema(implementation = PlaceRegisterRequest.class))
			)
			@RequestBody PlaceRegisterRequest request
	);

	@Operation(
			summary = "장소 목록 조회",
			description = "등록된 장소 목록을 페이지네이션하여 조회합니다."
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "장소 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = CustomPageResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@ExampleObject(
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
					description = "인증 실패",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                                    {
                                        "status": "UNAUTHORIZED",
                                        "code": "T001",
                                        "message": "토큰 만료",
                                        "timestamp": "2024-02-18T14:30:00.000000"
                                    }
                                    """
							)
					)
			)
	})
	ResponseEntity<CustomPageResponse<PlaceResponse>> retrievePlace(
			@Parameter(
					description = "페이지 요청 정보 (page, size)",
					required = true
			)
			CustomPageRequest pageRequest
	);
}