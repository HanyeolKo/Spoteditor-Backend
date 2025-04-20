package com.spoteditor.backend.modules.placelog.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spoteditor.backend.global.exception.PlaceLogException;

import static com.spoteditor.backend.global.response.ErrorCode.STATUS_NOT_SUPPORTED;

public enum PlaceLogStatus {
    PUBLIC,
    PRIVATE;

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static PlaceLogStatus fromJson(String value) {
        for (PlaceLogStatus status : PlaceLogStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new PlaceLogException(STATUS_NOT_SUPPORTED);
    }
}
