package com.spoteditor.backend.modules.user.controller;

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

    @PostMapping("/auth/refresh")
    public ResponseEntity<Void> refreshAccessToken (HttpServletRequest request, HttpServletResponse response) throws Exception {
        userTokenService.refreshAccessToken(request, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout (HttpServletResponse response) throws Exception {
        userTokenService.removeTokens(response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
