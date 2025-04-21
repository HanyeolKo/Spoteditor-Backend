package com.spoteditor.backend.modules.mapping.userplacelogmapping.repository;

public interface UserPlaceLogMappingRepositoryCustom {

    boolean existsByUserIdAndPlaceLogId(Long userId, Long placeLogId);
}
