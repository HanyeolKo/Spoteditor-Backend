package com.spoteditor.backend.modules.tag.repository;

import com.spoteditor.backend.modules.tag.entity.Tag;

import java.util.List;

public interface TagRepositoryCustom {

    List<Tag> findTagsByPlaceLogId(Long placeLogId);

    List<Tag> findByNameIn(List<String> tags);
}
