package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "warehouse", source = "warehouse.id")
    @Mapping(target = "active", source = "active")
    StoreDTO toDTO(Store store);

    List<StoreDTO> toDTOList(List<Store> stores);

    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "active", source = "active")
    Store toEntity(StoreDTO dto);
}
