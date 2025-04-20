package com.spoteditor.backend.modules.user.controller.dto;

import com.spoteditor.backend.modules.user.entity.User;

public record UserProfileImageDto (
        String imageUrl,
        Long imageId
) {
    public UserProfileImageDto(String imageUrl, Long imageId) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    public static UserProfileImageDto from(User user) {
        if(user.getUploadImage() == null) {
            return new UserProfileImageDto(
                    user.getImageUrl(),
                    null
            );
        } else {
            return new UserProfileImageDto(
                    user.getUploadImage().getStoredFile(),
                    user.getUploadImage().getId()
            );
        }
    }
}
