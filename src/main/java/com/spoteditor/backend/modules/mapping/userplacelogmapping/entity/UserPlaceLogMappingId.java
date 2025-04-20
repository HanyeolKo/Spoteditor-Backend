package com.spoteditor.backend.modules.mapping.userplacelogmapping.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class UserPlaceLogMappingId implements Serializable {
    private Long userId;
    private Long placeLogId;

    public UserPlaceLogMappingId(Long userId, Long placeLogId) {
        this.userId = userId;
        this.placeLogId = placeLogId;
    }
}
