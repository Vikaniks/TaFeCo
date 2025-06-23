package com.tafeco.Models.Services;

import com.tafeco.Models.Entity.Order;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "twilio")
@Getter
@Setter
@Slf4j
public class WhatsAppNotificationSender {

    private String accountSid;
    private String authToken;
    private String whatsappFromNumber;
    private String whatsappToNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio инициализирован с AccountSid: {}", accountSid);
    }

    public void send(Order order) {
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(whatsappToNumber),
                new com.twilio.type.PhoneNumber(whatsappFromNumber),
                "Новый заказ #" + order.getId() +
                        ", сумма: " + order.getTotalPrice() + " руб."
        ).create();

        log.info("WhatsApp сообщение отправлено с SID: {}", message.getSid());
    }
}

