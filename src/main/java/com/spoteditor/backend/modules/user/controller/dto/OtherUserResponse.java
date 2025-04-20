package com.spoteditor.backend.modules.user.controller.dto;

import com.spoteditor.backend.modules.user.service.dto.OtherUserResult;

public record OtherUserResponse(
        Long userId,
        String name,
        String instagramId,
        UserProfileImageDto profileImage,
        String description,
        Long follower,
        Long following,
        boolean isFollowing
) {
    public static OtherUserResponse from(OtherUserResult result) {
        return new OtherUserResponse(
                result.userId(),
                result.name(),
                result.instagramId(),
                result.profileImage(),
                result.description(),
                result.follower(),
                result.following(),
                result.isFollowing()
        );
    }
}
