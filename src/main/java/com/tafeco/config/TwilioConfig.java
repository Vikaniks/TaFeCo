package com.tafeco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.ToString;

@Component
@Getter
@ToString
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    @Value("${twilio.whatsapp.to}")
    private String whatsappTo;

}

