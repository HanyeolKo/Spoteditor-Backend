package com.spoteditor.backend.modules.user.service.dto;

public record UserUpdateCommand (
        String name,
        String originalFile,
        String uuid,
        String description,
        String instagramId
) {
}
