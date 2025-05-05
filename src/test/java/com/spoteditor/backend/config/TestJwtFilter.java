package com.spoteditor.backend.config;


import com.spoteditor.backend.modules.user.common.dto.UserIdDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


public class TestJwtFilter extends OncePerRequestFilter {

    @Override
    protected  void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Long userId = extractUserIdFromCookie(request);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new UserIdDto(userId),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private Long extractUserIdFromCookie(HttpServletRequest request) {
        if(request.getCookies() == null) return 0L;

        for(Cookie cookie : request.getCookies()) {
            if("AccessToken".equals(cookie.getName())) {
                try {
                    return Long.parseLong(cookie.getValue());
                } catch (NumberFormatException e ) {
                    return 0L;
                }
            }
        }
        return 0L;
    }
}
