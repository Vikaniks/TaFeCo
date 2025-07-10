package com.tafeco.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/css/**",
                                "/api/products",
                                "/api/products/category/**",
                                "/category",
                                "/api/categories/**",
                                "/uploads/**",
                                "/js/**",
                                "/img/**",
                                "/fonts/**",
                                "/shop",
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/**",
                                "/register",
                                "/sale",
                                "/condition",
                                "/cart",
                                "/confirmar_order",
                                "/finalOrder",
                                "/order",
                                "/api/orders",
                                "/error",
                                "/favicon.ico",
                                "/admin",
                                "/admin/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/user/**"
                        ).hasRole("USER")
                        .requestMatchers(
                                "/api/moderator",
                                "/api/dimensions/**",
                                "/api/categories/**",
                                "/api/orders",
                                "/api/moderator/products/**",
                                "/api/moderator/warehouses/**",
                                "/api/moderator/stores/**",
                                "/api/warehouses/**",
                                "/api/stores/**"
                        ).hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }
}

