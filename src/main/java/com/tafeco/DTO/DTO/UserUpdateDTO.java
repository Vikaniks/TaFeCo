package com.tafeco.DTO.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
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

    // Новый пароль (если пользователь хочет его изменить)
    private String newPassword;

    // Текущий пароль — для подтверждения смены
    private String currentPassword;

    private Boolean temporaryPassword;
}

