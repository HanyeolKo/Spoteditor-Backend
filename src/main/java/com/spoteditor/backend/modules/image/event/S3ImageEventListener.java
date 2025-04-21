package com.spoteditor.backend.modules.image.event;

import com.spoteditor.backend.modules.image.service.PlaceImageService;
import com.spoteditor.backend.modules.image.event.dto.S3Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class S3ImageEventListener {

    private final PlaceImageService placeImageService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleRollbackEvent(S3ImageRollbackEvent event) {
        for(S3Image rollbackFile : event.rollbackFiles()) {
            placeImageService.rollbackUpload(rollbackFile);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommitEvent(S3ImageAfterCommitEvent event) {
        for(S3Image deleteFile : event.deleteFiles()) {
            placeImageService.deleteMainBucketImage(deleteFile);
        }
    }
}
