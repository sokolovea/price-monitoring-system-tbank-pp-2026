package ru.tbank.pp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.tbank.pp.service.TgBotService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TgBotConfig {
    private final TgBotService tgBotService;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            var tgApi = new TelegramBotsApi(DefaultBotSession.class);
            tgApi.registerBot(tgBotService);
        } catch (TelegramApiException ex) {
            log.error(ex.getMessage());
        }
    }
}