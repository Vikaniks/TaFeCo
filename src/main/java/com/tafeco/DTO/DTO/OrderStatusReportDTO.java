package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusReportDTO {
    private OrderStatus status;
    private Long count;

}
