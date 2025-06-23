package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserRegisterDTO;
import com.tafeco.DTO.DTO.UserUpdateDTO;
import com.tafeco.Models.Entity.OrderStatus;
import com.tafeco.Models.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {

    UserDTO register(UserRegisterDTO dto);

    UserDTO updateUser(String username, UserUpdateDTO updateDTO);
    UserDTO getUserProfile(String username);
    void deleteUser(String username);

    List<UserDTO> getAllUsers();
    void updateUserRole(String username, String role);
    Page<UserDTO> getUsersWithFilters(String username, String email, Pageable pageable);

    User findByUsername(String username);

    void updateOrderStatus(Integer orderId, String newStatus);
    Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void resetPassword(String email);




}

