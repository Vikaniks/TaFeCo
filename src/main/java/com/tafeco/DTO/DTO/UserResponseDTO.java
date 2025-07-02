package com.tafeco.DTO.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;

    private String name;
    private String surname;
    private String phone;
    private String email;

    // Адресные поля
    private String locality;
    private String district;
    private String region;
    private String street;
    private String house;
    private String apartment;
    private String addressExtra;

    private boolean active;
    private Set<String> roles;
}

