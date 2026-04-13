package ru.tbank.pp.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notification-api")
public class NotificationApiProperties {
    private String baseUrl;
}
