package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserRegisterDTO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "tempPasswordExpiration", ignore = true)
    User fromRegisterDTO(UserRegisterDTO dto);

    // Основной метод
    default UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setDeliveryAddress(user.getDeliveryAddress());
        dto.setActive(user.isActive());

        Set<String> roleNames = user.getRoles().stream()
                .map(RoleUser::getRole)
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);

        return dto;
    }
}
