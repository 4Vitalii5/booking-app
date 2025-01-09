package org.cyberrealm.tech.service.impl;

import org.cyberrealm.tech.service.NotificationService;
import org.cyberrealm.tech.service.TelegramClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramNotificationService implements NotificationService {
    @Value("${telegram.chat.id}")
    private String chatId;
    private final TelegramClient telegramClient;

    public TelegramNotificationService(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void sendNotification(String message) {
        telegramClient.sendMessage(chatId, message);
    }
}
