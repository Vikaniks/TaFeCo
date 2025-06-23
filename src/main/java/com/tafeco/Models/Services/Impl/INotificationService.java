package com.tafeco.Models.Services.Impl;

import com.tafeco.Models.Entity.Order;

public interface INotificationService {
    void notifyManager(Order order);
    void notifyUser(String toEmail, String message);
    void sendTemporaryPassword(String to, String subject, String message);
}
