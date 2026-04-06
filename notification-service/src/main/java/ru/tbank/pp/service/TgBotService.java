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
import ru.tbank.pp.client.BackendClient;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;
import ru.tbank.pp.properties.TgBotProperties;

import java.util.List;

import static ru.tbank.pp.model.ServiceConnectionService.TELEGRAM;

@Slf4j
@Service
public class TgBotService extends TelegramWebhookBot {
    private final TgBotProperties tgBotProperties;
    private final BackendClient backendClient;

    private static final String PRICE_DROP_CAPTION = "💰 Цена на товар '%s' упала!";
    private static final String USER_LINK = "Привязка к пользователю %d прошла успешно!";
    private static final String USER_LINK_ERROR = "Ошибка привязки к пользователю! Такой ID уже зарегистрирован!";
    private static final String VIEW_PRODUCT_BUTTON_TEXT = "Посмотреть товар";

    public TgBotService(TgBotProperties tgBotProperties,
                        BackendClient backendClient) throws TelegramApiException {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
        this.backendClient = backendClient;
        this.setWebhook(new SetWebhook(tgBotProperties.getBaseUrl()));
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

                        var serviceConnectionStatusCheckRequest = new ServiceConnectionStatusCheckRequest();
                        serviceConnectionStatusCheckRequest.setService(TELEGRAM);
                        serviceConnectionStatusCheckRequest.setId(userId);

                        if (!backendClient.checkIfUserExists(serviceConnectionStatusCheckRequest)) {
                            var serviceConnectionConnectRequest = new ServiceConnectionConnectRequest();
                            serviceConnectionConnectRequest.setService(TELEGRAM);
                            serviceConnectionConnectRequest.setId(userId);
                            serviceConnectionConnectRequest.setInternalId(chatId);

                            backendClient.connectUserService(serviceConnectionConnectRequest);
                            sendMessage.setText(String.format(USER_LINK, userId));
                        }
                        else {
                            sendMessage.setText(USER_LINK_ERROR);
                        }

                    } catch (NumberFormatException e) {
                        log.warn("Invalid user ID format: {}", parts[1]);
                    }

                }

            }

            return sendMessage;
        }

        return null;
    }

    public void executeNotification(NotificationRequestDto notificationRequestDto) throws TelegramApiException {
        var button = InlineKeyboardButton.builder()
                .text(VIEW_PRODUCT_BUTTON_TEXT)
                .url(notificationRequestDto.getProductUrl())
                .build();

        var keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();

        execute(SendPhoto.builder()
                .chatId(notificationRequestDto.getChatId())
                .photo(new InputFile(notificationRequestDto.getProductPhotoUrl()))
                .caption(String.format(PRICE_DROP_CAPTION, notificationRequestDto.getProductName()))
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
