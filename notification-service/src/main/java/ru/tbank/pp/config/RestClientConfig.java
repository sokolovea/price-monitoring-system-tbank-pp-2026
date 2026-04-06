package ru.tbank.pp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.properties.ApiProperties;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final ApiProperties apiProperties;
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(apiProperties.getBaseUrl())
                .build();
    }
}