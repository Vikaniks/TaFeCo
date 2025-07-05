package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private Map<OrderStatus, Long> ordersByStatus;

}
