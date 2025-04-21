package com.spoteditor.backend.modules.mapping.placelogplacemapping.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_log_place_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceLogPlaceMapping extends BaseEntity {

    @EmbeddedId
    private PlaceLogPlaceMappingId id;

    @MapsId("placeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @MapsId("placeLogId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_log_id")
    private PlaceLog placeLog;

    @Builder
    private PlaceLogPlaceMapping(PlaceLog placeLog, Place place) {
        this.id = new PlaceLogPlaceMappingId(placeLog.getId(), place.getId());
        this.placeLog = placeLog;
        this.place = place;
    }
}
