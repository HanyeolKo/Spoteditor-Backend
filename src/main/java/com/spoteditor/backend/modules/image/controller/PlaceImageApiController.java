package com.spoteditor.backend.modules.image.controller;

import com.spoteditor.backend.config.swagger.docs.PlaceImageApiDocument;
import com.spoteditor.backend.modules.image.controller.dto.PreSignedUrlResponse;
import com.spoteditor.backend.modules.image.controller.dto.PreSignedUrlRequest;
import com.spoteditor.backend.modules.image.service.PlaceImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "image", description = "이미지(첨부파일) API")
public class PlaceImageApiController implements PlaceImageApiDocument {

	private final PlaceImageService imageService;

	/**
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/purl")
	public ResponseEntity<PreSignedUrlResponse> processPreSignedUrlResponse(@RequestBody PreSignedUrlRequest request) {

		PreSignedUrlResponse presignedUrlResponse = imageService.processPreSignedUrl(request);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(presignedUrlResponse);
	}
}
