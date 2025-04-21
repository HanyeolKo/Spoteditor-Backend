package com.spoteditor.backend.modules.place.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.global.exception.PlaceException;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;
import com.spoteditor.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.spoteditor.backend.global.response.ErrorCode.NOT_PLACE_IMAGE;

@Entity
@Getter
@Table(name = "place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "place_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "place", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<PlaceLogPlaceMapping> placeLogPlaceMappings = new ArrayList<>();

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "address")
	@Embedded
	private Address address;

	@Column(name = "category")
	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(name = "bookmark")
	private int bookmark;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceImage> placeImages = new ArrayList<>();

	@Version
	private Long version;

	@Builder
	private Place(User user, Category category, Address address, String description, String name) {
		this.user = user;
		this.address = address;
		this.description = description;
		this.name = name;
		this.category = category;
	}

	public void addPlaceImage(PlaceImage placeImage) {
		this.placeImages.add(placeImage);
		if (placeImage.getPlace() != this) {
			placeImage.addPlace(this);
		}
	}

	public PlaceImage deletePlaceImage(Long placeImageId) {
		Iterator<PlaceImage> iterator = this.placeImages.iterator();
		while(iterator.hasNext()) {
			PlaceImage placeImage = iterator.next();
			if(placeImage.getId().equals(placeImageId)) {
				iterator.remove();
				return placeImage;
			}
		}
		throw new PlaceException(NOT_PLACE_IMAGE);
	}

	public void updateDescription(String description) {
		if(description != null) this.description = description;
	}

	public void increaseBookmark() {
		this.bookmark++;
	}

	public void decreaseBookmark() {
		if (this.bookmark <= 0) {
			throw new IllegalArgumentException("북마크 개수는 0 미만의 값을 가질 수 없습니다.");
		}
		this.bookmark--;
	}

}
