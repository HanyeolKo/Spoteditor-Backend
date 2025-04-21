package com.spoteditor.backend.modules.tag.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @OneToMany(mappedBy = "tag", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PlaceLogTagMapping> placeLogTagMappings = new ArrayList<>();

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private TagCategory tagCategory;
}
