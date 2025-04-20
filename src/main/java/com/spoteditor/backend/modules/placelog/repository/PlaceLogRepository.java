package com.spoteditor.backend.modules.placelog.repository;

import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLogRepository extends JpaRepository<PlaceLog, Long>, PlaceLogRepositoryCustom {
}
