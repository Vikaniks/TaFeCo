package com.tafeco.DTO.Mappers;


import com.tafeco.DTO.DTO.WarehouseDTO;
import com.tafeco.Models.Entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring", uses = {StoreMapper.class})
public interface WarehouseMapper {

    @Mapping(source = "stores", target = "stores")
    WarehouseDTO toDTO(Warehouse warehouse);


    Warehouse toEntity(WarehouseDTO dto);
    List<WarehouseDTO> toDTOList(List<Warehouse> warehouses);
}

