package com.spoteditor.backend.modules.follow.repository;

import com.spoteditor.backend.modules.follow.entity.Follow;
import com.spoteditor.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

	Optional<Follow> findFollowByFollowerAndFollowing(User follower, User following);

	void deleteByFollowerAndFollowing(User follower, User following);

	@Modifying
	@Query("DELETE FROM Follow follow WHERE follow.follower = :follower")
	void deleteAllByFollower(User follower);

	@Modifying
	@Query("DELETE FROM Follow follow WHERE follow.following = :following")
	void deleteAllByFollowing(User following);
}
