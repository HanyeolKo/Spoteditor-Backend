package com.spoteditor.backend.modules.placebookmark.repository;

import com.spoteditor.backend.modules.placebookmark.entity.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

	Optional<PlaceBookmark> findByUserIdAndPlaceId(@Param("userId") Long userId, @Param("placeId") Long placeId);
	List<Long> findBookmarkedPlaceIdsByUserId(@Param("userId") Long userId);
}
