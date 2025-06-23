package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransferRequestDTO {
    private String productName;
    private int quantityToTransfer;
    private Long targetStoreId;
    private Long sourceWarehouseId;
}

