package mate.academy.service.impl;

import mate.academy.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TelegramNotificationService implements NotificationService {
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;
    private final WebClient webClient = WebClient.create("https://api.telegram.org/bot" + botToken);

    @Override
    public void sendNotification(String message) {
        webClient.post()
                .uri("sendMessage?chat_id=" + chatId + "&text=" + message)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
