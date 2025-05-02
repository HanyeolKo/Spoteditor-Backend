package com.spoteditor.backend.modules.placelog.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.mapping.placelogplacemapping.entity.PlaceLogPlaceMapping;
import com.spoteditor.backend.modules.mapping.placelogtagmapping.entity.PlaceLogTagMapping;
import com.spoteditor.backend.modules.mapping.userplacelogmapping.entity.UserPlaceLogMapping;
import com.spoteditor.backend.modules.place.entity.Address;
import com.spoteditor.backend.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "place_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "placeLog", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserPlaceLogMapping> userPlaceLogMappings = new ArrayList<>();

    @OneToMany(mappedBy = "placeLog", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PlaceLogPlaceMapping> placeLogPlaceMappings = new ArrayList<>();

    @OneToMany(mappedBy = "placeLog", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PlaceLogTagMapping> placeLogTagMappings = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private PlaceImage placeLogImage;

    @Column(name = "address")
    @Embedded
    private Address address;

    @Column(name = "views")
    private long views;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlaceLogStatus status;

    @Version
    private Long version;

    @Builder
    private PlaceLog(User user, String name, String description, PlaceImage placeLogImage, Address address, PlaceLogStatus status) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.placeLogImage = placeLogImage;
        this.address = address;
        this.views = 0L;
        this.status = status;
    }

    public void update(String name, String description, PlaceLogStatus status) {
        if (name != null && !name.trim().isEmpty()) this.name = name;
        if (description != null) this.description = description;
        if (status != null) this.status = status;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void deleteImage() {
        if(this.placeLogImage != null) {
            this.placeLogImage = null;
        }
    }

    public void addImage(PlaceImage placeLogImage) {
        this.placeLogImage = placeLogImage;
    }
}
