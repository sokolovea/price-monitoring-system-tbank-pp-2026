package ru.tbank.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.properties.TgBotProperties;
import ru.tbank.service.TgBotService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TgBotConfig {

    private final TgBotProperties tgBotProperties;
    private final TgBotService tgBotService;

    @PostConstruct
    public void registerWebhook() {
        try {
            log.info("Registering webhook...");

            SetWebhook setWebhook = SetWebhook.builder()
                    .url(tgBotProperties.getBaseUrl())
                    .build();

            tgBotService.setWebhook(setWebhook);

        } catch (TelegramApiException e) {
            log.error("Failed to register webhook", e);
        }
    }
}