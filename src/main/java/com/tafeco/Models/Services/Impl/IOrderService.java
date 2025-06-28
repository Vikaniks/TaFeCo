package com.tafeco.Models.Services.Impl;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.OrderDetailDTO;
import com.tafeco.Models.Entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;

public interface IOrderService {
    OrderDTO create(OrderDTO dto, String username);
    OrderDTO getById(int id);
    List<OrderDTO> getAll();
    void deleteOrder(int id);

    List<OrderDTO> getByStatus(OrderStatus status);
    List<OrderDTO> getByUserId(Long user);
    List<OrderDTO> getByUserIdAndStatuses(Long user, List<OrderStatus> statuses);

    OrderDetailDTO getOrderDetailById(int order);

    Page<OrderDTO> getAllOrders(OrderStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void updateOrderStatus(Integer order, String newStatus);

    OrderDTO changeOrderStatus(Integer orderId, OrderStatus newStatus);

}

