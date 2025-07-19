package com.tafeco.controllers;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Services.Impl.IOrderService;
import com.tafeco.Models.Services.Impl.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final IUserService userService;
    private final IOrderService orderService;



    @GetMapping
    public ResponseEntity<String> checkAdminAccess() {
        return ResponseEntity.ok("Admin access confirmed");
    }


    // Получить профиль конкретного пользователя
    @GetMapping("/users/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @GetMapping("/users/email")
    public ResponseEntity<UserDTO> getUserProfile(@RequestParam String value) {
        UserDTO user = userService.getUserProfile(value);
        return ResponseEntity.ok(user);
    }
    // Поиск по телефону
    @GetMapping("/users/phone")
    public ResponseEntity<UserDTO> getUserPhone(@RequestParam String value) {
        UserDTO user = userService.getUserPhone(value);
        return ResponseEntity.ok(user);
    }

    // Удалить пользователя по email
    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }

    // назначение роли
    @PostMapping("/users/{email}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable String email,
                                               @RequestParam String role) {
        userService.updateUserRole(email, role); // метод нужно реализовать в сервисе
        return ResponseEntity.ok().build();
    }

    // Получить список всех пользователей
    @GetMapping("/users/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<UserDTO> users = userService.getUsersWithFilters(fullName, phone, address, PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }


    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getAllOrders(status, startDate, endDate, pageable);
        return ResponseEntity.ok(orders);
    }
}
