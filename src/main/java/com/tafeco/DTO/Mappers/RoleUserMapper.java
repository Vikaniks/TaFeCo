package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.RoleUserDTO;
import com.tafeco.Models.Entity.RoleUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleUserMapper {
    RoleUserDTO toDTO(RoleUser role);
    RoleUser toEntity(RoleUserDTO dto);
}

