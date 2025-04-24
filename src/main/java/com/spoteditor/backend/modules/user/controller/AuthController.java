package com.spoteditor.backend.modules.user.controller;

import com.spoteditor.backend.global.constants.JwtConstants;
import com.spoteditor.backend.global.utils.CookieUtil;
import com.spoteditor.backend.infrastructure.jwt.JwtUtils;
import com.spoteditor.backend.modules.user.service.UserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserTokenService userTokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/auth/refresh")
    public ResponseEntity<Void> refreshAccessToken (HttpServletRequest request, HttpServletResponse response) throws Exception {

        String refreshToken = cookieUtil.getRefreshToken(request);

        String accessToken = userTokenService.reissueAccessToken(refreshToken);

        if (accessToken != null) {
            cookieUtil.setAccessTokenCookie(response, JwtConstants.ACCESS_TOKEN, accessToken);

            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout (HttpServletRequest request, HttpServletResponse response) throws Exception {

        String refreshToken = cookieUtil.getRefreshToken(request);

        userTokenService.removeRefreshTokenOnRedis(refreshToken);

        cookieUtil.removeCookie(response, "/", JwtConstants.ACCESS_TOKEN);
        cookieUtil.removeCookie(response, "/", JwtConstants.REFRESH_TOKEN);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
