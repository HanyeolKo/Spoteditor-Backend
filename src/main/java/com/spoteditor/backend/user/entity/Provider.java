package com.spoteditor.backend.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {

    KAKAO("kakao"),
    GOOGLE("google");

    private final String provider;
}
