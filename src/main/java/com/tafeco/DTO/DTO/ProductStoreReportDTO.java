package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStoreReportDTO {
    private Long productId;
    private String productName;
    private Long storeId;
    private String storeLocation;
    private int quantity;
}


