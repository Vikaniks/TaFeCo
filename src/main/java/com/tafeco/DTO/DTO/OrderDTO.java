package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private int id;
    private LocalDate orderDate;
    private Long user;
    private String userEmail;
    private Set<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
}

