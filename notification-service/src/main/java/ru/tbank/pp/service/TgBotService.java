package ru.tbank.pp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

import java.io.File;
import java.net.URL;
import java.util.List;

import static ru.tbank.pp.model.ServiceConnectionService.TELEGRAM;

@Slf4j
@Service
public class TgBotService extends TelegramLongPollingBot {
    private final TgBotProperties tgBotProperties;
    private final BackendClient backendClient;

    private static final String PRICE_DROP_CAPTION = "💰 Цена на товар '%s' упала! ||$s||";
    private static final String USER_LINK = "Привязка к пользователю %d прошла успешно!";
    private static final String USER_LINK_ERROR = "Ошибка привязки к пользователю! Такой ID уже зарегистрирован!";
    private static final String VIEW_PRODUCT_BUTTON_TEXT = "Посмотреть товар";

    public TgBotService(TgBotProperties tgBotProperties,
                        BackendClient backendClient) throws TelegramApiException {
        super(tgBotProperties.getToken());
        this.tgBotProperties = tgBotProperties;
        this.backendClient = backendClient;
    }

    @Override
    public void onUpdateReceived(Update update) {
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
                        if (backendClient.checkIfUserExists(serviceConnectionStatusCheckRequest)) {
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

                    } catch (Exception e) {
                        log.warn(e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
            else {
                sendMessage.setText(text);
            }

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executeNotificationMessage(NotificationRequestDto notificationRequestDto) throws TelegramApiException {
        var button = InlineKeyboardButton.builder()
                .text(VIEW_PRODUCT_BUTTON_TEXT)
                .url(notificationRequestDto.getProductUrl())
                .build();

        var keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();

        var sendMessage = new SendMessage(
                notificationRequestDto.getChatId().toString(),
                String.format(PRICE_DROP_CAPTION, notificationRequestDto.getProductName())
        );
        sendMessage.setReplyMarkup(keyboard);
        execute(sendMessage);
    }

    public void executeNotification(NotificationRequestDto notificationRequestDto) throws TelegramApiException {
        var button = InlineKeyboardButton.builder()
                .text(VIEW_PRODUCT_BUTTON_TEXT)
                .url(notificationRequestDto.getProductUrl())
                .build();

        var keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();

        File tempFile = null;
        try {
            URL url = new URL(notificationRequestDto.getProductPhotoUrl());
            tempFile = File.createTempFile("product_", ".jpg");
            FileUtils.copyURLToFile(url, tempFile, 5000, 10000);

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(notificationRequestDto.getChatId())
                    .photo(new InputFile(tempFile))
                    .caption(String.format(PRICE_DROP_CAPTION, notificationRequestDto.getProductName(), notificationRequestDto.getProductPhotoUrl()))
                    .replyMarkup(keyboard)
                    .build();

            execute(sendPhoto);

        } catch (Exception e) {
            log.error("Failed to send photo", e);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(notificationRequestDto.getChatId())
                    .text(String.format(PRICE_DROP_CAPTION + "\n\n%s",
                            notificationRequestDto.getProductName(),
                            notificationRequestDto.getProductUrl()))
                    .replyMarkup(keyboard)
                    .build();
            execute(sendMessage);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return tgBotProperties.getName();
    }
}