package com.spoteditor.backend.modules.placelog.repository;

import com.spoteditor.backend.modules.placelog.entity.PlaceLog;
import com.spoteditor.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PlaceLogRepository extends JpaRepository<PlaceLog, Long>, PlaceLogRepositoryCustom {

    @Modifying
    @Query("DELETE FROM PlaceLog log WHERE log.user = :user")
    void deleteAllByUser(User user);

    List<PlaceLog> findAllByUser(User user);
}
