package com.spoteditor.backend.modules.mapping.placelogtagmapping.repository;

import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLogTagMappingRepository extends JpaRepository<PlaceLogTagMapping, PlaceLogTagMappingId>, PlaceLogTagMappingRepositoryCustom {

}
