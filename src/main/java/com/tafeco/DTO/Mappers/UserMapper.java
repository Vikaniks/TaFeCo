package com.tafeco.DTO.Mappers;

import com.tafeco.DTO.DTO.UserDTO;
import com.tafeco.DTO.DTO.UserRegisterDTO;
import com.tafeco.Models.Entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "tempPasswordExpiration", ignore = true)
    @Mapping(target = "address", expression = "java(mapToAddress(dto))")
    User fromRegisterDTO(UserRegisterDTO dto);

    @Mapping(target = "locality", expression = "java(user.getAddress() != null && user.getAddress().getLocality() != null ? user.getAddress().getLocality().getName() : null)")
    @Mapping(target = "district", expression = "java(user.getAddress() != null && user.getAddress().getDistrict() != null ? user.getAddress().getDistrict().getName() : null)")
    @Mapping(target = "region", expression = "java(user.getAddress() != null && user.getAddress().getRegion() != null ? user.getAddress().getRegion().getName() : null)")
    @Mapping(target = "street", expression = "java(user.getAddress() != null && user.getAddress().getStreet() != null ? user.getAddress().getStreet().getName() : null)")
    @Mapping(target = "house", expression = "java(user.getAddress() != null ? user.getAddress().getHouse() : null)")
    @Mapping(target = "apartment", expression = "java(user.getAddress() != null ? user.getAddress().getApartment() : null)")
    @Mapping(target = "addressExtra", expression = "java(user.getAddress() != null ? user.getAddress().getAddressExtra() : null)")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getRole()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "temporaryPassword", expression = "java(isTempPasswordValid(user))")
    UserDTO toDTO(User user);

    default Address mapToAddress(UserRegisterDTO dto) {
        if (dto == null) return null;
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

    default boolean isTempPasswordValid(User user) {
        return user.getTempPasswordExpiration() != null &&
                user.getTempPasswordExpiration().isAfter(java.time.LocalDateTime.now());
    }
}

