package com.tafeco.DTO.DTO;

import com.tafeco.Models.Entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long product;
    private String productName;
    private boolean productActive;
    private String dimensionName;
    private int quantity;
    private BigDecimal priceAtOrderTime;

    public String getDisplayName() {
        return productActive ? productName : productName + " (снят с продажи)";
    }
}
