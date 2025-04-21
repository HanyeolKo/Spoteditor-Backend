package com.spoteditor.backend.modules.image.event;

import com.spoteditor.backend.modules.image.event.dto.S3Image;

import java.util.List;

public record S3ImageAfterCommitEvent(
        List<S3Image> deleteFiles
) {
}