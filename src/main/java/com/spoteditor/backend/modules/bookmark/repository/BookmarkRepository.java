package com.spoteditor.backend.modules.bookmark.repository;

import com.spoteditor.backend.modules.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Optional<Bookmark> findByUserIdAndPlaceId(@Param("userId") Long userId, @Param("placeId") Long placeId);

	@Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bookmark b WHERE b.user.id = :userId AND b.place.id = :placeId")
	boolean existsByUserAndPlace(@Param("userId") Long userId, @Param("placeId") Long placeId);

	@Query("SELECT b.place.id FROM Bookmark b WHERE b.user.id = :userId")
	List<Long> findBookmarkedPlaceIdsByUserId(@Param("userId") Long userId);
}
