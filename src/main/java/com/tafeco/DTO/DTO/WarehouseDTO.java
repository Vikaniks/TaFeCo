package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long id;
    private String location;
    private Set<StoreDTO> stores;
}
