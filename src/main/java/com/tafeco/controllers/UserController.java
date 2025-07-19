package com.tafeco.controllers;


import com.tafeco.DTO.DTO.ChangePasswordRequest;
import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserUpdateDTO;
import com.tafeco.Models.Entity.User;
import com.tafeco.Models.Services.Impl.IProductService;
import com.tafeco.Models.Services.Impl.IUserService;
import com.tafeco.Security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER') or #authentication.principal.temporaryPassword == true")

@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtService jwtService;
    private final IProductService productService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserDTO userDTO = userService.getUserProfile(username);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateDTO updateDTO,
                                                 Authentication authentication) {
        String username = authentication.getName();
        UserDTO updated = userService.updateUser(username, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Authentication auth) {
        String email = auth.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok("Пароль изменён");
    }


}

