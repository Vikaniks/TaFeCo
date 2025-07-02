package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserRegisterDTO;
import com.tafeco.Models.Entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "tempPasswordExpiration", ignore = true)
    @Mapping(target = "address", expression = "java(mapToAddress(dto))")
    User fromRegisterDTO(UserRegisterDTO dto);

    default Address mapToAddress(UserRegisterDTO dto) {
        if (dto == null) {
            return null;
        }
        Address address = new Address();
        address.setLocality(mapLocality(dto.getLocality()));
        address.setDistrict(mapDistrict(dto.getDistrict()));
        address.setRegion(mapRegion(dto.getRegion()));
        address.setStreet(mapStreet(dto.getStreet()));
        address.setHouse(dto.getHouse());
        address.setApartment(dto.getApartment());
        address.setAddressExtra(dto.getAddressExtra());
        return address;
    }


    default Locality mapLocality(String name) {
        if (name == null) return null;
        Locality locality = new Locality();
        locality.setName(name);
        return locality;
    }

    default District mapDistrict(String name) {
        if (name == null) return null;
        District district = new District();
        district.setName(name);
        return district;
    }

    default Region mapRegion(String name) {
        if (name == null || name.isBlank()) return null;
        Region region = new Region();
        region.setName(name.trim());
        return region;
    }

    default Street mapStreet(String name) {
        if (name == null || name.isBlank()) return null;
        Street street = new Street();
        street.setName(name.trim());
        return street;
    }


    default UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());

        Address addr = user.getAddress();
        if (addr != null) {
            dto.setLocality(addr.getLocality() != null ? addr.getLocality().getName() : null);
            dto.setDistrict(addr.getDistrict() != null ? addr.getDistrict().getName() : null);
            dto.setRegion(addr.getRegion() != null ? addr.getRegion().getName() : null);
            dto.setStreet(addr.getStreet() != null ? addr.getStreet().getName() : null);
            dto.setHouse(addr.getHouse());
            dto.setApartment(addr.getApartment());
            dto.setAddressExtra(addr.getAddressExtra());
        }

        dto.setActive(user.isActive());

        Set<String> roleNames = user.getRoles().stream()
                .map(RoleUser::getRole)
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);

        return dto;
    }

}

