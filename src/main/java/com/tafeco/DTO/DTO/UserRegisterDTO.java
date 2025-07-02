package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;

    // Адресные поля
    private String locality;
    private String district;
    private String region;
    private String street;
    private String house;
    private String apartment;
    private String addressExtra;
}


