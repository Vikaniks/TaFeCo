package com.tafeco.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalVariables {

    @Value("${twilio.whatsapp.to}")
    private String whatsappTo;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${telegram.username}")
    private String telegramUsername;

    @ModelAttribute("whatsappTo")
    public String whatsappTo() {
        return whatsappTo;
    }

    @ModelAttribute("mailUsername")
    public String mailUsername() {
        return mailUsername;
    }

    @ModelAttribute("telegramUsername")
    public String telegramUsername() {
        return telegramUsername;
    }
}

