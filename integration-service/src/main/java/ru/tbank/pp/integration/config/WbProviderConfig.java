package ru.tbank.pp.integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.provider.wildberries")
public class WbProviderConfig {
    private String token;
    private Long dest;
}
