package com.tafeco.DTO.DTO;


import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    private Long id;
    private int maxQuantity;
    private int currentQuantity;
    private Long product;            // id продукта
    private Long warehouse;          // id склада
    private String storeName;
    private String location;
    private boolean active;
}

