package com.spoteditor.backend.modules.user.controller.dto;

import com.spoteditor.backend.modules.user.service.dto.UserUpdateResult;

public record UserUpdateResponse (
        String email,
        String name,
        UserProfileImageDto profileImage,
        String description,
        String instagramId
) {
    public static UserUpdateResponse from(UserUpdateResult result) {
        return new UserUpdateResponse(
                result.user().getEmail(),
                result.user().getName(),
                UserProfileImageDto.from(result.user()),
                result.user().getDescription(),
                result.user().getInstagramId()
        );
    }
}
