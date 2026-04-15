package ru.tbank.pp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.properties.IntegrationApiProperties;
import ru.tbank.pp.properties.NotificationApiProperties;
import ru.tbank.pp.properties.YandexGptProperties;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final NotificationApiProperties notificationApiProperties;
    private final IntegrationApiProperties integrationApiProperties;
    private final YandexGptProperties yandexGptProperties;

    @Bean
    @Qualifier("notification")
    public RestClient getNotificationRestClient() {
        return RestClient.builder()
                .baseUrl(notificationApiProperties.getBaseUrl())
                .build();
    }

    @Bean
    @Qualifier("integration")
    public RestClient getIntegrationRestClient() {
        return RestClient.builder()
                .baseUrl(integrationApiProperties.getBaseUrl())
                .build();
    }

    @Bean
    @Qualifier("yandex")
    public RestClient getYandexRestClient() {
        return RestClient.builder()
                .baseUrl(yandexGptProperties.getApiUrl())
                .build();
    }

}