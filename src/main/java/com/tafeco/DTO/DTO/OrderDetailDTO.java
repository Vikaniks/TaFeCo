package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    private int id;
    private LocalDate orderDate;
    private UserDTO user;
    private Set<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
}
