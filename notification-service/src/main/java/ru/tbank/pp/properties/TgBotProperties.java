package ru.tbank.pp.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bot")
public class TgBotProperties {
    String baseUrl;
    String webhookPath;
    String name;
    String token;
}
