package ru.tbank.pp.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "yandex")
public class YandexGptProperties {
    String apiUrl;
    String apiKey;
    String folderId;
    String commandPath;
    String model;
}