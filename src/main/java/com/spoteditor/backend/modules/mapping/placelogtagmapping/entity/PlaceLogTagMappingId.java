package com.spoteditor.backend.modules.mapping.placelogtagmapping.entity;

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
public class PlaceLogTagMappingId implements Serializable {
    private Long placeLogId;
    private Long tagId;

    public PlaceLogTagMappingId(Long placeLogId, Long tagId) {
        this.placeLogId = placeLogId;
        this.tagId = tagId;
    }
}
