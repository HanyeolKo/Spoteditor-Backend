package com.spoteditor.backend.modules.user.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.image.entity.PlaceImage;
import com.spoteditor.backend.modules.mapping.userplacelogmapping.entity.UserPlaceLogMapping;
import com.spoteditor.backend.modules.place.entity.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserPlaceLogMapping> userPlaceLogMappings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Place> userPlace = new ArrayList<>();

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private PlaceImage uploadImage;

    @Column(name = "description")
    private String description;

    @Column(name = "instagram_id")
    private String instagramId;

    @Enumerated(value = EnumType.STRING)    // 사용자/관리자
    @Column(name = "role")
    private Role role;

    @Column(name = "provider")              // 제공자
    private String provider;

    @Column(name = "provider_id")           // 고유 ID
    private String providerId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Builder
    private User(String email, String name, String imageUrl, String provider, String providerId, Role role) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    // 통합 로그인을 위한 이메일 업데이트 메서드
    public void updateOauthInfo(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

    public void update(String name, String description, String instagramId) {
        if (name != null && !name.trim().isEmpty()) this.name = name;
        if (description != null) this.description = description;
        if (instagramId != null) this.instagramId = instagramId;
    }

    public void deleteImage() {
        if(this.uploadImage != null) {
            this.uploadImage = null;
        }
    }

    public void addImage(PlaceImage placeImage) {
        this.uploadImage = placeImage;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}