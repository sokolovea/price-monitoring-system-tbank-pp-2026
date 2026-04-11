package ru.tbank.pp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.pp.config.RestClientConfig;

@Component
@RequiredArgsConstructor
public class NotificationClient {
    private final RestClient restClient;

    public void sendNotification(NotificationRequestDto notificationRequestDto) {
        restClient
                .post()
                .uri("/webhook/send")
                .body(notificationRequestDto)
                .retrieve()
                .body(void.class);
    }
}
