package com.tafeco.Security;

import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Environment environment;

    @Value("${JWT_SECRET:}")
    private String secret;

    private SecretKey secretKey;

    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        boolean isTestProfile = Arrays.asList(environment.getActiveProfiles()).contains("test");

        if (secret == null || secret.length() < 32) {
            if (!isTestProfile) {
                throw new IllegalStateException("JWT_SECRET env variable is missing or too short (min 32 chars)");
            } else {
                secret = "00000000000000000000000000000000"; // fallback для тестов
            }
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        System.out.println("JWT_SECRET initialized.");
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(RoleUser::getRole)
                .collect(Collectors.toList()));

        boolean isTemporaryPassword = user.getTempPasswordExpiration() != null &&
                user.getTempPasswordExpiration().isAfter(LocalDateTime.now());
        claims.put("temporaryPassword", isTemporaryPassword);

        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
