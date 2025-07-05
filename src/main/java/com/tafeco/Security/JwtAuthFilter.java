package com.tafeco.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        System.out.println("🔍 Заголовок Authorization: " + authHeader);

        // Проверка на наличие и начало с Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⛔ Authorization отсутствует или не начинается с Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("🪙 JWT: " + jwt);

        username = jwtService.extractUsername(jwt);
        System.out.println("📧 Извлечён username/email из токена: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("✅ Загрузили userDetails: " + userDetails.getUsername());

            System.out.println("Authorities from UserDetails: " + userDetails.getAuthorities());

            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("🔐 Токен валиден, создаём аутентификацию...");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("🔓 Authority: " + authToken.getAuthorities());

                System.out.println("✅ Пользователь аутентифицирован: " + userDetails.getUsername());
            } else {
                System.out.println("⛔ Токен НЕвалиден");
            }
        } else {
            System.out.println("⚠ Либо username null, либо пользователь уже аутентифицирован");
        }

        filterChain.doFilter(request, response);
    }
}