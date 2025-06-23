package com.tafeco.Models.Services;

import com.tafeco.DTO.DTO.OrderDTO;
import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Services.Impl.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    private final EmailNotificationSender emailSender;
    private final WhatsAppNotificationSender whatsappSender;


    @Override
    public void notifyManager(Order order) {
        emailSender.send(order);
        whatsappSender.send(order);
    }

    @Override
    public void notifyUser(String toEmail, String message) {
        emailSender.notifyUser(toEmail, message);
    }

    @Override
    public void sendTemporaryPassword(String to, String subject, String message) {
        emailSender.sendTemporaryPassword(to, subject, message);
    }



}

