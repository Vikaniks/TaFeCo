package com.tafeco.DTO.DTO;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String email;
    private String fullName;
    private String deliveryAddress;

    // Новый пароль (если пользователь хочет его изменить)
    private String newPassword;

    // Текущий пароль — для подтверждения смены
    private String currentPassword;
}

