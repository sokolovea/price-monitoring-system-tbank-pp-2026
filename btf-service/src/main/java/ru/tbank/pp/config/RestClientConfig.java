package ru.tbank.pp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.properties.NotificationApiProperties;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final NotificationApiProperties notificationApiProperties;
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(notificationApiProperties.getBaseUrl())
                .build();
    }
}