package com.tafeco.DTO.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long product;
    private int quantity;
    private BigDecimal priceAtOrderTime;
    private Long orderId;
}
