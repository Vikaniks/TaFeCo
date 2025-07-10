package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.CreateStoreProductDTO;
import com.tafeco.DTO.DTO.StoreProductDTO;
import com.tafeco.Models.Entity.Product;
import com.tafeco.Models.Entity.Store;
import com.tafeco.Models.Entity.StoreProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoreProductMapper {

    @Mapping(source = "product.id", target = "product")
    @Mapping(source = "store.storeName", target = "storeName")
    StoreProductDTO toDTO(StoreProduct entity);

    List<StoreProductDTO> toDTOList(List<StoreProduct> list);

    @Mapping(target = "id", ignore = true)
    StoreProduct toEntity(CreateStoreProductDTO dto, @Context Product product, @Context Store store);
}