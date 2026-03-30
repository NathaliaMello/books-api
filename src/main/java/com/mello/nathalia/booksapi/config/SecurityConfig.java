package com.mello.nathalia.booksapi.config;

import com.mello.nathalia.booksapi.domain.service.UserService;
import com.mello.nathalia.booksapi.infrastructure.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String ROLE_ADMIN = "ADMIN";
    private static final String BOOKS_PATH = "/api/v1/books/**";
    private static final String CATEGORIES_PATH = "/api/v1/categories/**";
    private static final String BOOK_RATING_PATH = "/api/v1/books/*/rating";

    private static final String[] PUBLIC_PATHS = {
            "/auth/**",
            "/docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // rotas públicas
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, BOOKS_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, CATEGORIES_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, BOOKS_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, BOOKS_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, BOOKS_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, CATEGORIES_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, CATEGORIES_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, CATEGORIES_PATH).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH,BOOK_RATING_PATH).hasRole(ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
