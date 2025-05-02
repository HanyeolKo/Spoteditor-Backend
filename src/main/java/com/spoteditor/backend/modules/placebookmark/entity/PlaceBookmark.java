package com.spoteditor.backend.modules.placebookmark.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.place.entity.Place;
import com.spoteditor.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "PLACE_BOOKMARK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceBookmark extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "place_bookmark_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	@Builder
	private PlaceBookmark(User user, Place place) {
		this.user = user;
		this.place = place;
	}
}
