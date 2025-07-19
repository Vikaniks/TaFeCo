package com.tafeco.Models.Services;

import com.tafeco.Models.Entity.Order;
import com.tafeco.Models.Services.Impl.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    private final EmailNotificationSender emailSender;
    private final WhatsAppNotificationSender whatsappSender;


    @Async
    @Override
    public void notifyManager(Order order) {
        try {
            emailSender.send(order);
            whatsappSender.send(order);
        } catch (Exception e) {
            log.warn("❗ Ошибка при уведомлении менеджера: {}", e.getMessage());
        }
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

