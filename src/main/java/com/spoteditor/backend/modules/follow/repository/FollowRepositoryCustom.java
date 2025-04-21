package com.spoteditor.backend.modules.follow.repository;

import com.spoteditor.backend.global.page.CustomPageRequest;
import com.spoteditor.backend.global.page.CustomPageResponse;
import com.spoteditor.backend.modules.follow.controller.dto.FollowResponse;

public interface FollowRepositoryCustom {

	CustomPageResponse<FollowResponse> findAllFollower(Long userId, CustomPageRequest request);
	CustomPageResponse<FollowResponse> findAllFollowing(Long userId, CustomPageRequest request);

	long countFollower(Long userId);
	long countFollowing(Long userId);

	boolean findIsFollowing(Long userId, Long otherUserId);
}
