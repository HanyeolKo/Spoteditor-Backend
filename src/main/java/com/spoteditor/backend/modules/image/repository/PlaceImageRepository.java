package com.spoteditor.backend.modules.image.repository;

import com.spoteditor.backend.modules.image.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {
}
