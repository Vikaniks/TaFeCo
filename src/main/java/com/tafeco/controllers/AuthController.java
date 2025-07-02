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
import java.util.stream.Collectors;


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

            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOptional.get();

            // Получаем первую роль
            String role = user.getRoles().stream()
                    .map(RoleUser::getRole)
                    .findFirst()
                    .orElse("USER");

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


            return ResponseEntity.ok(new LoginResponseDTO(token, role, userDTO));

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
    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        userService.resetPassword(request.getEmail());
        return ResponseEntity.ok("Временный пароль отправлен на вашу почту.");
    }
}
