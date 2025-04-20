package com.spoteditor.backend.modules.bookmark.service;

import com.spoteditor.backend.modules.bookmark.service.dto.BookmarkCommand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkService {

	void addBookmark(Long userId, BookmarkCommand command);
	void removeBookmark(Long userId, BookmarkCommand command);

	@Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bookmark b WHERE b.user.id = :userId AND b.place.id = :placeId")
	boolean existsByUserIdAndPlaceId(@Param("userId") Long userId, @Param("placeId") Long placeId);
}
