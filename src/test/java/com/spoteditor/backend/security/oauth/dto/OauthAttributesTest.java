package com.spoteditor.backend.security.oauth.dto;

import com.spoteditor.backend.global.exception.UserException;
import com.spoteditor.backend.user.entity.Provider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.spoteditor.backend.global.response.ErrorCode.UNSUPPORTED_PROVIDER;
import static org.junit.jupiter.api.Assertions.*;

class OauthAttributesTest {

    @Test
    @DisplayName("지원되지않는 Provider 입력시 예외")
    public void 지원되지않는_Provider_테스트() {
        // given
        String unsupportedProvider = "facebook";
        Map<String, Object> attributes = Map.of(
                "id", "1234567890",
                "kakao_account", Map.of(
                        "email", "testuser@kakao.com"
                ),
                "properties", Map.of(
                        "nickname", "테스트유저",
                        "profile_image", "http://example.com/profile.jpg"
                )
        );

        // when
        UserException exception = assertThrows(UserException.class, () -> {
            OauthAttributes.of(unsupportedProvider, attributes);
        });

        // then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(UNSUPPORTED_PROVIDER);
    }

    @Test
    @DisplayName("카카오 속성 파싱 확인")
    public void 카카오속성_파싱_테스트() {
        // given
        Map<String, Object> attributes = Map.of(
                "id", "1234567890",
                "kakao_account", Map.of(
                        "email", "testuser@kakao.com"
                ),
                "properties", Map.of(
                        "nickname", "테스트유저",
                        "profile_image", "http://example.com/profile.jpg"
                )
        );

        // when
        OauthAttributes attribute = OauthAttributes.of("kakao", attributes);

        // then
        Assertions.assertThat(attribute.getOauthUserId()).isEqualTo("1234567890");
        Assertions.assertThat(attribute.getName()).isEqualTo("테스트유저");
        Assertions.assertThat(attribute.getEmail()).isEqualTo("testuser@kakao.com");
        Assertions.assertThat(attribute.getImageUrl()).isEqualTo("http://example.com/profile.jpg");
        Assertions.assertThat(attribute.getProvider()).isEqualTo(Provider.KAKAO);
    }

    @Test
    @DisplayName("속성 누락시에도 오류 나지 않는부분 확인")
    public void 속성누락_테스트() {
        // given
        Map<String, Object> incompleteAttributes = Map.of(
                "id", "1234567890",
                "kakao_account", Map.of(),
                "properties", Map.of(
                        "nickname", "테스트유저"
                )
        );

        // when
        OauthAttributes attribute = OauthAttributes.of("kakao", incompleteAttributes);

        // then
        Assertions.assertThat(attribute.getOauthUserId()).isEqualTo("1234567890");
        Assertions.assertThat(attribute.getName()).isEqualTo("테스트유저");
        Assertions.assertThat(attribute.getEmail()).isNull(); // Email 누락 확인
        Assertions.assertThat(attribute.getImageUrl()).isNull(); // Profile Image 누락 확인
    }
}