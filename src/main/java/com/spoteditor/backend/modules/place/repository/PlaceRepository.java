package com.spoteditor.backend.modules.place.repository;

import com.spoteditor.backend.modules.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, PlaceRepositoryCustom {

}
