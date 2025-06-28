package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.OrderItemDTO;
import com.tafeco.Models.Entity.OrderItem;
import com.tafeco.Models.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "product")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(source = "product", target = "product")
    OrderItem toEntity(OrderItemDTO dto);

    default Product map(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }


}
