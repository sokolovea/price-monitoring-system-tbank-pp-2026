package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.dto.RequestDto;
import ru.tbank.properties.TgBotProperties;

import java.util.List;

@Slf4j
@Service
public class TgBotService extends TelegramWebhookBot {
    private final TgBotProperties tgBotProperties;

    public TgBotService(TgBotProperties tgBotProperties) {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Received update: {}", update);
        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getChatId();
            var text = message.getText();

            log.info("Message from {}: {}", chatId, text);

            var sendMessage = new SendMessage(chatId.toString(), "");

            if (text.startsWith("/start")) {
                String[] parts = text.split(" ", 2);
                if (parts.length > 1) {
                    var userId = Integer.parseInt(text.split(" ")[1]);
                    sendMessage.setText("Привязка к пользователю " + userId);

                    //todo проверка регистрации
//                if (backendClient.isUserRegistered(userId)) {
//                    backendClient.connectService(userId, TELEGRAM, chatId);
//                    sendMessage.setText("Пользователь привязан");
//                }
//                else {
//                    sendMessage.setText("Ошибка в привязке к пользователю");
//                }
                }

            }

            return sendMessage;
        }

        return null;
    }

    public void executeNotification(RequestDto requestDto) throws TelegramApiException {
        var button = InlineKeyboardButton.builder()
                .text("Посмотреть товар")
                .url(requestDto.getProductUrl())
                .build();

        var keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();

        execute(SendPhoto.builder()
                .chatId(requestDto.getChatId())
                .photo(new InputFile(requestDto.getProductPhoto()))
                .caption("Цена на товар '" + requestDto.getProductName() + "' упала!")
                .replyMarkup(keyboard).build());
    }

    @Override
    public String getBotPath() {
        return tgBotProperties.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}
