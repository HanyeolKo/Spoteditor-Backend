package com.spoteditor.backend.modules.mapping.placelogplacemapping.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PlaceLogPlaceMappingId implements Serializable {
    private Long placeId;
    private Long placeLogId;

    public PlaceLogPlaceMappingId(Long placeId, Long placeLogId) {
        this.placeId = placeId;
        this.placeLogId = placeLogId;
    }
}
