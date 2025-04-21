package com.spoteditor.backend.modules.mapping.placelogtagmapping.entity;


import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_log_tag_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceLogTagMapping extends BaseEntity {

    @EmbeddedId
    private PlaceLogTagMappingId id;

    @MapsId("placeLogId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_log_id")
    private PlaceLog placeLog;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private PlaceLogTagMapping(PlaceLog placeLog, Tag tag) {
        this.id = new PlaceLogTagMappingId(placeLog.getId(), tag.getId());
        this.placeLog = placeLog;
        this.tag = tag;
    }
}
