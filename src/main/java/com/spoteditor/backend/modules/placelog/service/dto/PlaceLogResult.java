package com.spoteditor.backend.modules.placelog.service.dto;

import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.tag.entity.Tag;

import java.util.List;

public record PlaceLogResult(
        PlaceLog placeLog,
        List<Tag> tags,
        List<Place> places
) {
    public static PlaceLogResult from(PlaceLog placeLog, List<Place> places, List<Tag> tags) {
        return new PlaceLogResult(
                placeLog,
                tags,
                places
        );
    }
}
