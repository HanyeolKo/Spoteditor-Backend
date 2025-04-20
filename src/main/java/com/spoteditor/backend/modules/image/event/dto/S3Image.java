package com.spoteditor.backend.modules.image.event.dto;

import com.spoteditor.backend.modules.image.controller.dto.PlaceImageResponse;
import com.spoteditor.backend.modules.image.entity.PlaceImage;

public record S3Image(
        String originalFile,
        String uuid,
        String storedFile
) {
    public static S3Image from(PlaceImageResponse response, String uuid) {
        return new S3Image(
                response.originalFile(),
                uuid,
                response.storedFile()
        );
    }

    public static S3Image from(PlaceImage image) {
        return new S3Image(
                null,
                null,
                image.getStoredFile()
        );
    }
}
