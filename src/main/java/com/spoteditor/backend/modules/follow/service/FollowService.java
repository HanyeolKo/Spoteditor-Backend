package com.spoteditor.backend.modules.follow.service;

import com.spoteditor.backend.modules.follow.controller.dto.FollowRequest;

public interface FollowService {

	void saveFollow(Long userId, FollowRequest request);
	void removeFollow(Long userId, FollowRequest request);
}
