package ru.tbank.pp.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration-api")
public class IntegrationApiProperties {
    private String baseUrl;
}
