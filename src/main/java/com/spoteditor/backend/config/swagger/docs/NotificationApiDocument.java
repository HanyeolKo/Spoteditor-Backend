package com.spoteditor.backend.config.swagger.docs;

import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.global.response.ErrorResponse;
import com.spoteditor.backend.modules.notification.controller.dto.NotificationListDto;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "notification", description = "알림 API")
public interface NotificationApiDocument {

	@Operation(summary = "알림 목록 조회", description = "페이지네이션된 알림 목록을 조회합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "알림 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = CustomPageResponse.class, subTypes = {NotificationListDto.class}))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "유효하지 않은 입력 값",
									value = """
                        {
                            "status": "BAD_REQUEST",
                            "code": "C004",
                            "message": "invalid type value",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
							))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									name = "권한 없음",
									value = """
                        {
                            "status": "UNAUTHORIZED",
                            "code": "U003",
                            "message": "현재 사용자의 권한이 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
							))
			)
	})
	ResponseEntity<List<NotificationListDto>> noticeList(
			@Parameter(description = "인증된 사용자 정보", required = true)
			@AuthenticationPrincipal UserIdDto dto
	);

	@Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "204",
					description = "알림 읽음 처리 성공"
			),
			@ApiResponse(
					responseCode = "404",
					description = "알림을 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                        {
                            "status": "NOT_FOUND",
                            "code": "N001",
                            "message": "알림을 찾을 수 없습니다.",
                            "timestamp": "2024-02-18T14:30:00.000000"
                        }
                        """
							))
			)
	})
	ResponseEntity<Void> read(
			@Parameter(description = "알림 ID", required = true)
			@PathVariable Long notificationId
	);

	@Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
	@ApiResponses({
			@ApiResponse(
					responseCode = "204",
					description = "모든 알림 읽음 처리 성공",
					content = @Content  // 명시적으로 빈 content 지정
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(
									value = """
                            {
                                "status": "UNAUTHORIZED",
                                "code": "U003",
                                "message": "현재 사용자의 권한이 없습니다.",
                                "timestamp": "2024-02-18T14:30:00.000000"
                            }
                            """
							)
					)
			)
	})
	ResponseEntity<Void> readAll(
			@Parameter(description = "인증된 사용자 정보", required = true)
			@AuthenticationPrincipal UserIdDto dto
	);
}