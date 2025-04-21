package com.spoteditor.backend.modules.user.controller.dto;

import com.spoteditor.backend.modules.user.service.dto.UserResult;

public record UserResponse(
        Long userId,
        String name,
        String instagramId,
        UserProfileImageDto profileImage,
        String description,
        Long follower,
        Long following
) {
    public static UserResponse from(UserResult userResult) {
        return new UserResponse(
                userResult.user().getId(),
                userResult.user().getName(),
                userResult.user().getInstagramId(),
                UserProfileImageDto.from(userResult.user()),
                userResult.user().getDescription(),
                userResult.follower(),
                userResult.following()
        );
    }
}
