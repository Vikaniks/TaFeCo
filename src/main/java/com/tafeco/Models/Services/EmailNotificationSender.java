package com.tafeco.Models.Services;

import com.tafeco.Models.Entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationSender {
    private final JavaMailSender mailSender;

    public void send(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vikaniks@mail.ru"); // адрес менеджера
        message.setSubject("Новый заказ #" + order.getId());
        message.setText(
                "Поступил новый заказ.\n" +
                        "Номер: " + order.getId() + "от" + order.getOrderDate() + "\n" +
                        "Товар: " + order.getItems() + "\n" +
                        "Сумма: " + order.getTotalPrice() + " руб."
        );

        mailSender.send(message);
    }

    public void notifyUser(String toEmail, String message) {
        var mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject("Order status update");
        mail.setText(message);
        mailSender.send(mail);
    }



    public void sendTemporaryPassword(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tafeco.ferma@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


}

