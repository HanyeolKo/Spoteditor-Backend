package com.spoteditor.backend.security.oauth.service;

import com.spoteditor.backend.user.entity.User;
import com.spoteditor.backend.user.entity.Provider;
import com.spoteditor.backend.user.repository.UserRepository;
import com.spoteditor.backend.config.oauth.dto.OauthAttributesUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OauthUserResolverTest {

    @Autowired private UserRepository userRepository;
    @Autowired private OauthAttributesUserMapper oauthAttributesUserMapper;
    @Autowired private OauthUserResolver oauthUserResolver;

    @Test
    @DisplayName("resolveUserId 로 새로운 계정 등록")
    @Transactional
    void 새로운_유저_생성_테스트() {
        // given
        Map<String, Object> attributes = Map.of(
                "id", "9876543210",
                "kakao_account", Map.of(
                        "email", "newuser@kakao.com"
                ),
                "properties", Map.of(
                        "nickname", "새로운유저",
                        "profile_image", "http://example.com/newuser.jpg"
                )
        );
        OauthAttributes attribute = OauthAttributes.of("kakao", attributes);

        // when
        Long createdUserId = oauthUserResolver.resolveUserId(attribute);

        // then
        assertNotNull(createdUserId);
        Optional<User> savedUser = userRepository.findByOauthUserIdAndProvider("9876543210", Provider.KAKAO);
        assertThat(savedUser).isPresent();
        User user = savedUser.get();
        assertThat(user.getId()).isEqualTo(createdUserId);
        assertThat(user.getEmail()).isEqualTo("newuser@kakao.com");
        assertThat(user.getName()).isEqualTo("새로운유저");
    }

    @Test
    @DisplayName("resolveUserId 로 기존유저 반환")
    @Transactional
    void 기존유저_반환_테스트() {
        // given
        User existingUser = User.builder()
                .oauthUserId("1234567890")
                .email("existinguser@kakao.com")
                .name("기존유저")
                .imageUrl("http://example.com/existinguser.jpg")
                .provider(Provider.KAKAO)
                .build();
        userRepository.save(existingUser);

        Map<String, Object> attributes = Map.of(
                "id", "1234567890",
                "kakao_account", Map.of(
                        "email", "existinguser@kakao.com"
                ),
                "properties", Map.of(
                        "nickname", "기존유저",
                        "profile_image", "http://example.com/existinguser.jpg"
                )
        );
        OauthAttributes attribute = OauthAttributes.of("kakao", attributes);

        // when
        Long returnedUserId = oauthUserResolver.resolveUserId(attribute);

        // then
        assertNotNull(returnedUserId);
        assertThat(returnedUserId).isEqualTo(existingUser.getId());
    }
}