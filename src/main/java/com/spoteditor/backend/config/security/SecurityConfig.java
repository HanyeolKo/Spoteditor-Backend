package com.spoteditor.backend.config.security;

import com.spoteditor.backend.infrastructure.jwt.JwtFilter;
import com.spoteditor.backend.infrastructure.jwt.JwtUtils;
import com.spoteditor.backend.infrastructure.oauth.handler.OauthFailureHandler;
import com.spoteditor.backend.infrastructure.oauth.handler.OauthSuccessHandler;
import com.spoteditor.backend.infrastructure.oauth.resolver.CustomOAuth2AuthorizationRequestResolver;
import com.spoteditor.backend.infrastructure.oauth.service.PrincipalOauth2UserService;
import com.spoteditor.backend.global.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    private final CorsConfigurationSource corsConfigurationSource;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthorizationRequestRepository<OAuth2AuthorizationRequest> authRequestRespsitory,
                                           OauthSuccessHandler successHandler,
                                           OauthFailureHandler failureHandler) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtFilter(jwtUtils, cookieUtils), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                    // LB분산 서버 처리, oauth 요청 처리를 위한 요청 저장 repository
                    .authorizationEndpoint(auth -> auth
                            .authorizationRequestResolver(new CustomOAuth2AuthorizationRequestResolver(
                                    clientRegistrationRepository,
                                    OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
                                )
                            )
                            .authorizationRequestRepository(authRequestRespsitory))
                    .userInfoEndpoint(userInfoEndpointConfig ->
                            userInfoEndpointConfig.userService(principalOauth2UserService))
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
            );

        return http.build();
    }
}
