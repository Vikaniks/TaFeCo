package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStockDTO {
    private Long productId;
    private String productName;
    private String categoryType;
    private int currentQuantity;
    private boolean active;
}