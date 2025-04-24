package com.spoteditor.backend.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spoteditor.backend.global.utils.CookieUtil;
import com.spoteditor.backend.global.exception.TokenException;
import com.spoteditor.backend.global.response.ErrorCode;
import com.spoteditor.backend.global.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.spoteditor.backend.global.constants.JwtWhiteList.*;
import static com.spoteditor.backend.global.response.ErrorCode.INVALID_ACCESS_TOKEN;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CookieUtil cookieUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isPathInWhiteList(ALL_METHOD_WHITE_LIST, path)) {
            return true;
        }

        return method.equals("GET") && isPathInWhiteList(GET_METHOD_WHITE_LIST, path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 쿠키에서 jwt 추출
        String accessToken = cookieUtil.getAccessToken(request);
        log.info(accessToken);

        String path = request.getRequestURI();

        try {
            // 토큰 인증
            UsernamePasswordAuthenticationToken authentication = jwtUtils.setAuthentication(accessToken);

            // 인증정보 SecurityContextHolder 에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch(ExpiredJwtException | MalformedJwtException | SignatureException e) {
            handleException(response, new TokenException(INVALID_ACCESS_TOKEN));
        } catch(IllegalArgumentException e) {
            if(isPathInWhiteList(GUEST_WHITE_LIST, path)) {
                filterChain.doFilter(request, response);
            } else {
                handleException(response, new TokenException(INVALID_ACCESS_TOKEN));
            }
        }
    }

    private void handleException(HttpServletResponse response, TokenException tokenException) throws IOException {
        ErrorCode errorCode = tokenException.getErrorCode();

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = objectMapper.writeValueAsString(ErrorResponse.of(errorCode));
        response.getWriter().write(jsonResponse);
    }

    private boolean isPathInWhiteList(List<String> whiteList, String path) {
        return whiteList.stream().anyMatch(pattern -> matchesPattern(pattern, path));
    }

    private boolean matchesPattern(String pattern, String path) {
        if(pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);

            return path.startsWith(prefix)
                    && path.length() > prefix.length()
                    && path.charAt(prefix.length()) == '/';
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            String[] pathParts = path.split("/");

            return path.startsWith(prefix)
                    && path.length() > prefix.length()
                    && path.charAt(prefix.length()) == '/'
                    && pathParts.length == prefix.split("/").length + 1 ;
        }
        return path.equals(pattern);
    }
}

