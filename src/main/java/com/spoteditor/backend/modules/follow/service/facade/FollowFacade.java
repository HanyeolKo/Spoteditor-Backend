package com.spoteditor.backend.modules.follow.service.facade;

import com.spoteditor.backend.global.aop.annotation.DistributedLock;
import com.spoteditor.backend.modules.follow.controller.dto.FollowRequest;
import com.spoteditor.backend.modules.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowFacade {

	private final FollowService followService;

	@DistributedLock(key = "#request.userId()")
	public void saveFollow(Long userId, FollowRequest request) {
		followService.saveFollow(userId, request);
	}

	@DistributedLock(key = "#request.userId()")
	public void removeFollow(Long userId, FollowRequest request) {
		followService.removeFollow(userId, request);
	}
}
