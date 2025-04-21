package com.spoteditor.backend.modules.image.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.place.entity.Place;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "place_image")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@Column(name = "original_file")
	private String originalFile;

	@Column(name = "stored_file")
	private String storedFile;

	@Builder
	private PlaceImage(Place place, String originalFile, String storedFile) {
		this.place = place;
        this.originalFile = originalFile;
        this.storedFile = storedFile;
	}

	public void addPlace(Place place) {
		this.place = place;

		if (!place.getPlaceImages().contains(this)) {
			place.getPlaceImages().add(this);
		}
	}
}
