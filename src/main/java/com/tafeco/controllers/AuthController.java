package com.tafeco.controllers;

import com.tafeco.DTO.DTO.*;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.Address;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import com.tafeco.Models.Services.Impl.IUserService;
import com.tafeco.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserDAO userRepository;
    private final IUserService userService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Установка контекста безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получаем email из аутентификации (username)
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtService.generateToken(user);

            // Получаем первую роль
            String role = user.getRoles().stream()
                    .map(RoleUser::getRole)
                    .findFirst()
                    .orElse("USER");

            // Проверка временного пароля
            boolean temporaryPassword = user.getTempPasswordExpiration() != null &&
                    user.getTempPasswordExpiration().isAfter(LocalDateTime.now());

            // Маппинг в UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setSurname(user.getSurname());
            userDTO.setPhone(user.getPhone());
            userDTO.setEmail(user.getEmail());

            Address addr = user.getAddress();
            if (addr != null) {
                userDTO.setLocality(addr.getLocality() != null ? addr.getLocality().getName() : null);
                userDTO.setDistrict(addr.getDistrict() != null ? addr.getDistrict().getName() : null);
                userDTO.setRegion(addr.getRegion() != null ? addr.getRegion().getName() : null);
                userDTO.setStreet(addr.getStreet() != null ? addr.getStreet().getName() : null);
                userDTO.setHouse(addr.getHouse());
                userDTO.setApartment(addr.getApartment());
                userDTO.setAddressExtra(addr.getAddressExtra());
            }

            userDTO.setActive(user.isActive());

            userDTO.setRoles(user.getRoles().stream()
                    .map(RoleUser::getRole)
                    .collect(Collectors.toSet()));

            return ResponseEntity.ok(new LoginResponseDTO(token, role, userDTO, temporaryPassword));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        LoginResponseDTO loginResponse = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    // Endpoint "Забыл пароль"
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        boolean temporaryPassword;
        try {
            userService.resetPassword(request.getEmail());
            temporaryPassword = true;
        } catch (UsernameNotFoundException e) {
            temporaryPassword = false;
        }

        return ResponseEntity.ok(Map.of(
                "message", "Если такой пользователь существует, временный пароль отправлен на вашу почту.",
                "temporaryPasswordSet", temporaryPassword
        ));
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getUserRoles(Authentication authentication) {
        System.out.println("✅ roles called by " + (authentication != null ? authentication.getName() : "null"));
        List<String> roles = userService.getCurrentUserRoles(authentication);
        return ResponseEntity.ok(roles);
    }


}
