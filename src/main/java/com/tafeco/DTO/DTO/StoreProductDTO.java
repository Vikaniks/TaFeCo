package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreProductDTO {
    private Long id;
    private int currentQuantity;

    private Long product;           // id продукта
    private Long warehouse;         // id склада
    private String storeName;       // название магазина
    private String warehouseLocation; // адрес склада
    private boolean active;
}
