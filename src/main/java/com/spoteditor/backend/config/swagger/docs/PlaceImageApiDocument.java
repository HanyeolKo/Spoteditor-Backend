package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.image.controller.dto.PreSignedUrlRequest;
import com.spoteditor.backend.modules.image.controller.dto.PreSignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface PlaceImageApiDocument {

	@Operation(
			summary = "Pre-signed URL 생성",
			description = "이미지 업로드를 위한 pre-signed URL을 생성합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "201",
					description = "Pre-signed URL 생성 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = PreSignedUrlResponse.class)
					)
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
					responseCode = "404",
					description = "이미지를 찾을 수 없음",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = {
									@io.swagger.v3.oas.annotations.media.ExampleObject(
											value = """
                        {
                            "status": "NOT_FOUND",
                            "code": "I001",
                            "message": "이미지를 찾을 수 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
									)
							}
					)
			)
	})
	ResponseEntity<PreSignedUrlResponse> processPreSignedUrlResponse(
			@Parameter(
					description = "원본 파일명",
					required = true,
					schema = @Schema(implementation = PreSignedUrlRequest.class)
			) PreSignedUrlRequest request
	);
}