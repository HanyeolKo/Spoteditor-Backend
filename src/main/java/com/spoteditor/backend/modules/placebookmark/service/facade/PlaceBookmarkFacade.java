package com.spoteditor.backend.modules.placebookmark.service.facade;

import com.spoteditor.backend.global.aop.annotation.DistributedLock;
import com.spoteditor.backend.modules.placebookmark.repository.PlaceBookmarkRepository;
import com.spoteditor.backend.modules.placebookmark.service.PlaceBookmarkService;
import com.spoteditor.backend.modules.placebookmark.service.dto.PlaceBookmarkCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceBookmarkFacade {

	private final PlaceBookmarkService placeBookmarkService;
	private final PlaceBookmarkRepository placeBookmarkRepository;

	@DistributedLock(key = "#command.placeId()", waitTime = 10L, leaseTime = 5L)
	public void addPlaceBookmark(Long userId, PlaceBookmarkCommand command) {
		placeBookmarkService.addPlaceBookmark(userId, command);
	}

	@DistributedLock(key = "#command.placeId()", waitTime = 10L, leaseTime = 5L)
	public void removePlaceBookmark(Long userId, PlaceBookmarkCommand command) {
		placeBookmarkService.removePlaceBookmark(userId, command);
	}

	// ❗해당 메서드는 개발된 상태라 바로 수정이 불가능해 차후 수정할 예정
	public boolean checkPlaceBookmark(Long userId, Long placeId) {
		final boolean[] flag = {false};
		placeBookmarkRepository.findByUserIdAndPlaceId(userId, placeId)
				.ifPresentOrElse(placeBookmark -> flag[0] = true, () -> flag[0] = false);
		return flag[0];
	}
}
