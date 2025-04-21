package com.spoteditor.backend.modules.tag.dto;

import com.spoteditor.backend.modules.tag.entity.Tag;
import com.spoteditor.backend.modules.tag.entity.TagCategory;

public record TagDto (
        String name,
        TagCategory category
) {
    public static TagDto from(Tag tag) {
        return new TagDto(
                tag.getName(),
                tag.getTagCategory()
        );
    }
}
