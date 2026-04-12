package ru.tbank.pp.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.tbank.dto.NotificationRequestDto;

@Component
public class NotificationClient {
    private final RestClient restClient;

    public NotificationClient(@Qualifier("notification") RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendNotification(NotificationRequestDto notificationRequestDto) {
        restClient
                .post()
                .uri("/webhook/send")
                .body(notificationRequestDto)
                .retrieve()
                .body(void.class);
    }
}
