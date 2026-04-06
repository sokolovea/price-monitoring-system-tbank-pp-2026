package ru.tbank.pp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.pp.config.RestClientConfig;

@Component
@RequiredArgsConstructor
public class NotificationClient {
    private final RestClientConfig restClientConfig;

    public void sendNotification(NotificationRequestDto notificationRequestDto) {
        restClientConfig.restClient()
                .post()
                .uri("/webhook/send")
                .body(notificationRequestDto)
                .retrieve()
                .body(void.class);
    }
}
