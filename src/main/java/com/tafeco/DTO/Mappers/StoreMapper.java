package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.StoreDTO;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, WarehouseMapper.class})
public interface StoreMapper {

    /* Entity → DTO */
    @Mapping(source = "product.id", target = "product")
    @Mapping(source = "warehouse.id", target = "warehouse")
    StoreDTO toDTO(Store store);

    List<StoreDTO> toDTOList(List<Store> stores);

    /* DTO → Entity */
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "maxQuantity", source = "dto.maxQuantity")
    @Mapping(target = "currentQuantity", source = "dto.currentQuantity")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "warehouse", source = "warehouse")
    Store toEntity(StoreDTO dto, Product product, Warehouse warehouse);

    default Product map(Long id) {
        if (id == null) return null;
        Product p = new Product();
        p.setId(id);
        return p;
    }

    default Warehouse mapWarehouse(Long id) {
        if (id == null) return null;
        Warehouse w = new Warehouse();
        w.setId(id);
        return w;
    }

}
