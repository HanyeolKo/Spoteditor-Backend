package com.spoteditor.backend.modules.notification.controller;

import com.spoteditor.backend.config.swagger.docs.NotificationApiDocument;
import com.spoteditor.backend.modules.notification.controller.dto.NotificationListDto;
import com.spoteditor.backend.modules.notification.repository.NotificationRepository;
import com.spoteditor.backend.modules.notification.service.NotificationService;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "notification", description = "알림 API")
public class NotificationApiController implements NotificationApiDocument {

	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;

	@GetMapping("/notice")
	public ResponseEntity<List<NotificationListDto>> noticeList(@AuthenticationPrincipal UserIdDto dto) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(notificationRepository.notificationList(dto.getId()));
	}

	/**
	 *
	 * @param notificationId
	 * @return
	 */
	@PutMapping("/notice/{notificationId}")
	public ResponseEntity<Void> read(@PathVariable(name = "notificationId") Long notificationId) {

		notificationService.read(notificationId);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@PutMapping("/notice/all")
	public ResponseEntity<Void> readAll(@AuthenticationPrincipal UserIdDto dto) {

		notificationService.readAll(dto.getId());
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}
}
