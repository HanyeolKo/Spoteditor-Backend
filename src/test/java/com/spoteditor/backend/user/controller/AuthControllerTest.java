package com.spoteditor.backend.user.controller;

import com.spoteditor.backend.config.TestSecurityConfig;
import com.spoteditor.backend.modules.user.controller.AuthController;
import com.spoteditor.backend.modules.user.service.UserTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserTokenService userTokenService;

    @Test
    @DisplayName("RefreshToken 을 통해 AccessToken 재발급")
    public void 토큰_리프레시_테스트() throws Exception {
        // given
        String refreshToken = "test-refresh-token";
        String newAccessToken = "new-access-token";

        doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            Cookie cookie = new Cookie("AccessToken", newAccessToken);
            response.addCookie(cookie);
            return null;
        }).when(userTokenService).refreshAccessToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("RefreshToken", refreshToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("AccessToken"))
                .andExpect(cookie().value("AccessToken", newAccessToken));
    }
}