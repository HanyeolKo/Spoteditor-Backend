package com.spoteditor.backend.modules.place.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
public class Address {

	// final 필드로 설계 & setter 제공 X
	private final String address;
	private final String roadAddress;
	private final double latitude;
	private final double longitude;
	private final String sido;
	private final String bname;
	private final String sigungu;

	protected Address() {
		this.address = null;
		this.roadAddress = null;
		this.latitude = 0;
		this.longitude = 0;
		this.sido = null;
		this.bname = null;
		this.sigungu = null;
	}

	public Address(String address, String roadAddress, double latitude, double longitude, String sido, String bname, String sigungu) {
		this.address = address;
		this.roadAddress = roadAddress;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sido = sido;
		this.bname = bname;
		this.sigungu = sigungu;
	}
}
