package com.tafeco.Models.Services;

import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Entity.OrderItem;
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
        message.setTo("tafeco.ferma@gmail.com"); // адрес менеджера
        message.setSubject("Новый заказ №" + order.getId());

        // Формируем читаемый список товаров
        StringBuilder itemsBuilder = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            itemsBuilder.append("- ")
                    .append(item.getProduct().getProduct()) // название продукта
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" ед.изм.  по ")
                    .append(item.getPriceAtOrderTime())
                    .append(" руб.\n");
        }

        message.setText(
                "Поступил новый заказ.\n\n" +
                        "Номер: №" + order.getId() + "\n" +
                        "Дата: " + order.getOrderDate() + "\n" +
                        "Товары:\n" + itemsBuilder + "\n" +
                        "Итоговая сумма: " + order.getTotalPrice() + " руб."
        );

        mailSender.send(message);
    }


    public void notifyUser(String toEmail, String message) {
        var mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject("Изменен статус заказа");
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

