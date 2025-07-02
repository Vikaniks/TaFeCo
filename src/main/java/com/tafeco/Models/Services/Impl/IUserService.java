package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.*;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {

    LoginResponseDTO register(UserRegisterDTO dto);

    UserDTO updateUser(String email, UserUpdateDTO updateDTO);
    UserDTO getUserProfile(String email);
    void deleteUser(String email);

    List<UserDTO> getAllUsers();
    void updateUserRole(String email, String role);
    Page<UserDTO> getUsersWithFilters(String name, String email, Pageable pageable);

    User findByEmail(String email);

    void updateOrderStatus(Integer orderId, String newStatus);
    Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void resetPassword(String email);




}

