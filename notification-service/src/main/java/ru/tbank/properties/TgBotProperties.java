package ru.tbank.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "bot")
public class TgBotProperties {
    String baseUrl;
    String webhookPath;
    String name;
    String token;
}
