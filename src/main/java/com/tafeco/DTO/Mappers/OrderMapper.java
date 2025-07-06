package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.DTO.DTO.OrderItemDTO;
import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderItem;
import com.tafeco.Models.Entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {

    @Mapping(source = "user.id", target = "user")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(target = "items", qualifiedByName = "orderItemsToDTO")
    OrderDTO toDTO(Order order);

    @Mapping(source = "user", target = "user", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderDTO dto);

    @Named("orderItemsToDTO")
    default Set<OrderItemDTO> orderItemsToDTO(Set<OrderItem> items) {
        if (items == null) return new HashSet<>();
        return items.stream().map(this::mapItem).collect(Collectors.toSet());
    }

    @Mapping(source = "product.id", target = "product")
    @Mapping(source = "product.product", target = "productName")
    @Mapping(source = "product.dimension.dimension", target = "dimensionName")
    OrderItemDTO mapItem(OrderItem item);

    // Добавим map(Long → User)
    @Named("mapUserIdToUser")
    default User map(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }
}

