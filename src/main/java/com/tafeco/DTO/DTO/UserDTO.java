package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
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

    private LocalDateTime tempPasswordExpiration;

    private boolean temporaryPassword;

}

