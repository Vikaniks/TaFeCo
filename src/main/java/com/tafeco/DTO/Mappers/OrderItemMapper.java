package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.OrderItemDTO;
import com.tafeco.Models.Entity.OrderItem;
import com.tafeco.Models.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "product")
    @Mapping(source = "order.id", target = "orderId")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(source = "product", target = "product.id")
    @Mapping(source = "orderId", target = "order.id")
    OrderItem toEntity(OrderItemDTO dto);

    // метод для маппинга Long -> Product
    default Product map(Long productId) {
        if (productId == null) return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}

