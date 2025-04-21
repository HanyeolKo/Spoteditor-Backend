package com.spoteditor.backend.modules.place.entity;

import lombok.Getter;

@Getter
public enum Category {

	TOUR("관광"),
	RESTAURANT("음식점"),
	CAFE("카페");

	private final String name;

	Category(String name) {
		this.name = name;
	}
}
