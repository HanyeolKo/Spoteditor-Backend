package com.spoteditor.backend.modules.follow.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.global.exception.FollowException;
import com.spoteditor.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.spoteditor.backend.global.response.ErrorCode.SELF_FOLLOW_NOT_ALLOWED;

@Entity
@Getter
@Table(name = "follow")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id")
	private User follower;	// 팔로워

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "following_id")
	private User following; // 팔로잉

	@Builder
	private Follow(User following, User follower) {
		this.following = following;
		this.follower = follower;
	}

	// 자기 자신 팔로우 금지
	public void validateNotSelfFollow(User user1, User user2) {
		if (user1.getId().equals(user2.getId())) {
			throw new FollowException(SELF_FOLLOW_NOT_ALLOWED);
		}
	}
}
