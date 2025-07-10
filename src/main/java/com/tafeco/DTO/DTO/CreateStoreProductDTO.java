package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreProductDTO {
    private Long storeId;
    private Long productId;
    private int maxQuantity;
    private int currentQuantity;
}
