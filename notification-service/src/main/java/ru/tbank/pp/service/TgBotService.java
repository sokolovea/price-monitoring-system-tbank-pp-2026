package ru.tbank.pp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.pp.dto.RequestDto;
import ru.tbank.pp.properties.TgBotProperties;

import java.util.List;

@Slf4j
@Service
public class TgBotService extends TelegramWebhookBot {
    private final TgBotProperties tgBotProperties;

    private static final String PRICE_DROP_CAPTION = "💰 Цена на товар '%s' упала!";
    private static final String USER_LINK = "Привязка к пользователю %d";
    private static final String VIEW_PRODUCT_BUTTON_TEXT = "Посмотреть товар";

    public TgBotService(TgBotProperties tgBotProperties) throws TelegramApiException {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
        //this.setWebhook(new SetWebhook(tgBotProperties.getBaseUrl()));
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.debug("Received update: {}", update);
        if (update.hasMessage()) {
            var message = update.getMessage();
            var chatId = message.getChatId();
            var text = message.getText();

            log.debug("Message from {}: {}", chatId, text);

            var sendMessage = new SendMessage(chatId.toString(), "");

            if (text.startsWith("/start")) {
                var parts = text.split(" ");
                if (parts.length > 1) {
                    try {
                        var userId = Long.parseLong(parts[1]);
                        sendMessage.setText(String.format(USER_LINK, userId));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid user ID format: {}", parts[1]);
                    }
//                    todo проверка регистрации
//                    if (backendClient.isUserRegistered(userId)) {
//                        backendClient.connectService(userId, TELEGRAM, chatId);
//                        sendMessage.setText("Пользователь привязан");
//                    }
//                    else {
//                        sendMessage.setText("Ошибка в привязке к пользователю");
//                    }
                }

            } else {
                sendMessage.setText(text);
            }



            return sendMessage;
        }

        return null;
    }

    public void executeNotification(RequestDto requestDto) throws TelegramApiException {
        var button = InlineKeyboardButton.builder()
                .text(VIEW_PRODUCT_BUTTON_TEXT)
                .url(requestDto.getProductUrl())
                .build();

        var keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();

        execute(SendPhoto.builder()
                .chatId(requestDto.getChatId())
                .photo(new InputFile(requestDto.getProductPhotoUrl()))
                .caption(String.format(PRICE_DROP_CAPTION, requestDto.getProductName()))
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
