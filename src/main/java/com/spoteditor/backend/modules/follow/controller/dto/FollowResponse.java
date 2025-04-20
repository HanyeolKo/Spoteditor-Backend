package com.spoteditor.backend.modules.follow.controller.dto;

public record FollowResponse(
	Long userId,
	String name,
	String imageUrl
) {
}
