package org.cyberrealm.tech.service.impl;

import jakarta.annotation.PostConstruct;
import org.cyberrealm.tech.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TelegramNotificationService implements NotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;
    private WebClient webClient;

    @PostConstruct
    private void init() {
        this.webClient = WebClient.create("https://api.telegram.org/bot" + botToken);
    }

    @Override
    public void sendNotification(String message) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", message)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
