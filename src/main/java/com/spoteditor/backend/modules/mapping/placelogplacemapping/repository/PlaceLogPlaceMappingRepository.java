package com.spoteditor.backend.modules.mapping.placelogplacemapping.repository;

import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLogPlaceMappingRepository extends JpaRepository<PlaceLogPlaceMapping, PlaceLogPlaceMappingId>, PlaceLogPlaceMappingRepositoryCustom {


}
