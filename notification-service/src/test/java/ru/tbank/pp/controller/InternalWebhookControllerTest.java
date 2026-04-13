package ru.tbank.pp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.pp.service.TgBotService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты InternalWebhookController")
class InternalWebhookControllerTest {

    @Mock
    private TgBotService botService;

    @InjectMocks
    private InternalWebhookController internalWebhookController;

    private NotificationRequestDto notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = new NotificationRequestDto();
        notificationRequest.setChatId(123456789L);
        notificationRequest.setProductName("Test Product");
        notificationRequest.setProductPhotoUrl("https://example.com/photo.jpg");
        notificationRequest.setProductUrl("https://example.com/product");
    }

    @Test
    @DisplayName("should успешно отправить уведомление")
    void sendMessage_shouldSuccess() throws TelegramApiException {
        // given
        doNothing().when(botService).executeNotification(notificationRequest);

        // when
        internalWebhookController.sendMessage(notificationRequest);

        // then
        verify(botService, times(1)).executeNotification(notificationRequest);
    }

    @Test
    @DisplayName("should пробросить исключение при ошибке отправки уведомления")
    void sendMessage_shouldThrowException_whenServiceFails() throws TelegramApiException {
        // given
        doThrow(new TelegramApiException("Telegram API error")).when(botService).executeNotification(notificationRequest);

        // when & then
        assertThatThrownBy(() -> internalWebhookController.sendMessage(notificationRequest))
                .isInstanceOf(TelegramApiException.class)
                .hasMessageContaining("Telegram API error");

        verify(botService, times(1)).executeNotification(notificationRequest);
    }

    @Test
    @DisplayName("should вызвать сервис с правильным DTO")
    void sendMessage_shouldCallServiceWithCorrectDto() throws TelegramApiException {
        // given
        doNothing().when(botService).executeNotification(any(NotificationRequestDto.class));

        // when
        internalWebhookController.sendMessage(notificationRequest);

        // then
        verify(botService).executeNotification(argThat(dto ->
                dto.getChatId().equals(123456789L) &&
                dto.getProductName().equals("Test Product") &&
                dto.getProductPhotoUrl().equals("https://example.com/photo.jpg") &&
                dto.getProductUrl().equals("https://example.com/product")
        ));
    }
}
