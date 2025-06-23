package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.OrderDetailDTO;
import com.tafeco.Models.Entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, UserMapper.class, OrderItemMapper.class})
public interface OrderDetailMapper {

    OrderDetailDTO toDTO(Order order);

}
