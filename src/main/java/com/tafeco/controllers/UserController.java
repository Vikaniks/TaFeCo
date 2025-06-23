package com.tafeco.controllers;


import com.tafeco.DTO.DTO.ProductDTO;
import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserUpdateDTO;
import com.tafeco.Models.Services.Impl.IProductService;
import com.tafeco.Models.Services.Impl.IUserService;
import com.tafeco.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtService jwtService;
    private final IProductService productService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        UserDTO userDTO = userService.getUserProfile(username);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/profile")
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


}

