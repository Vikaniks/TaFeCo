package com.tafeco.controllers;

import com.tafeco.DTO.DTO.*;
import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import com.tafeco.Models.Services.Impl.IUserService;
import com.tafeco.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserDAO userRepository;
    private final IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            // Получаем пользователя и его роль
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOptional.get();
            String role = user.getRoles().stream()
                    .findFirst()
                    .map(RoleUser::getRole)
                    .orElse("USER");

            return ResponseEntity.ok(new LoginResponseDTO(token, role));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

     // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserRegisterDTO dto) {
        UserDTO registeredUser = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    // Endpoint "Забыл пароль"
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        userService.resetPassword(request.getEmail());
        return ResponseEntity.ok("Временный пароль отправлен на вашу почту.");
    }
}
