package com.spoteditor.backend.modules.placebookmark.controller;

import com.spoteditor.backend.modules.place.controller.validator.PlaceRequestValidator;
import com.spoteditor.backend.modules.placebookmark.controller.dto.PlaceBookmarkRequest;
import com.spoteditor.backend.modules.placebookmark.controller.dto.PlaceBookmarkResponse;
import com.spoteditor.backend.modules.placebookmark.service.facade.PlaceBookmarkFacade;
import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "bookmark", description = "공간 북마크 API")
public class PlaceBookmarkApiController {

	private final PlaceBookmarkFacade bookmarkFacade;
	private final PlaceRequestValidator placeValidator;

	// HTTP 요청/응답을 담당하는 컨트롤러에서 처리할 수 있도록 추가
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.addValidators(placeValidator);
	}

	@PostMapping("/place/bookmark")
	public ResponseEntity<Void> addPlaceBookmark(@AuthenticationPrincipal UserIdDto dto,
											@RequestBody PlaceBookmarkRequest request) throws InterruptedException {

		placeValidator.validatePlaceExists(request.placeId());
		bookmarkFacade.addPlaceBookmark(dto.getId(), request.toCommandDto());
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.build();
	}

	@DeleteMapping("/place/bookmark")
	public ResponseEntity<Void> removePlaceBookmark(@AuthenticationPrincipal UserIdDto dto,
											   @RequestBody PlaceBookmarkRequest request) throws InterruptedException {

		placeValidator.validatePlaceExists(request.placeId());
		bookmarkFacade.removePlaceBookmark(dto.getId(), request.toCommandDto());
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@GetMapping("/place/bookmark")
	public ResponseEntity<PlaceBookmarkResponse> checkPlaceBookmark(@AuthenticationPrincipal UserIdDto dto,
															  @RequestParam("placeId") Long placeId) {

		placeValidator.validatePlaceExists(placeId);
		boolean response = bookmarkFacade.checkPlaceBookmark(dto.getId(), placeId);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new PlaceBookmarkResponse(response));
	}
}
