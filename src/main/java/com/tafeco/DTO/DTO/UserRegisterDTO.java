package com.tafeco.DTO.DTO;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String deliveryAddress;
}

