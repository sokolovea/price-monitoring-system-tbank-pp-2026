package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.properties.TgBotProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotService extends TelegramWebhookBot {
    private final TgBotProperties tgBotProperties;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Received update: {}", update);

        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getChatId();
            var text = message.getText();

            log.info("Message from {}: {}", chatId, text);

            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Я получил: '" + text + "'")
                    .build();
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return tgBotProperties.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }

    public void sendMessage(Message message) throws TelegramApiException {
        execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text(message.getText())
                .build());
    }
}
