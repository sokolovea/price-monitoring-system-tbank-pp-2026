package ru.tbank.pp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.pp.service.TgBotService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Интеграционные тесты InternalWebhookController")
class InternalWebhookControllerIntegrationTest {

    private MockMvc mockMvc;
    private TgBotService tgBotService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        tgBotService = mock(TgBotService.class);
        objectMapper = new ObjectMapper();

        InternalWebhookController controller = new InternalWebhookController(tgBotService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

//    @Test
//    @DisplayName("should вернуть 200 при успешной отправке уведомления")
//    void sendMessage_shouldReturn200() throws Exception {
//        // given
//        NotificationRequestDto request = new NotificationRequestDto();
//        request.setChatId(123456789L);
//        request.setProductName("Test Product");
//        request.setProductPhotoUrl("https://example.com/photo.jpg");
//        request.setProductUrl("https://example.com/product");
//
//        doNothing().when(tgBotService).executeNotification(any());
//
//        // when & then
//        mockMvc.perform(post("/webhook/send")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(tgBotService).executeNotification(any());
//    }
//
//    @Test
//    @DisplayName("should пробросить исключение при ошибке Telegram API")
//    void sendMessage_shouldThrowException_whenTelegramApiError() throws Exception {
//        // given
//        NotificationRequestDto request = new NotificationRequestDto();
//        request.setChatId(123456789L);
//        request.setProductName("Test Product");
//        request.setProductPhotoUrl("https://example.com/photo.jpg");
//        request.setProductUrl("https://example.com/product");
//
//        doThrow(new TelegramApiException("API error")).when(tgBotService).executeNotification(any());
//
//        // when & then
//        assertThatThrownBy(() -> mockMvc.perform(post("/webhook/send")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))))
//                .hasCauseInstanceOf(TelegramApiException.class);
//    }
//
//    @Test
//    @DisplayName("should вызвать сервис с правильным DTO")
//    void sendMessage_shouldCallServiceWithCorrectDto() throws Exception {
//        // given
//        NotificationRequestDto request = new NotificationRequestDto();
//        request.setChatId(987654321L);
//        request.setProductName("Another Product");
//        request.setProductPhotoUrl("https://example.com/photo2.jpg");
//        request.setProductUrl("https://example.com/product2");
//
//        doNothing().when(tgBotService).executeNotification(any());
//
//        // when & then
//        mockMvc.perform(post("/webhook/send")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(tgBotService).executeNotification(argThat(dto ->
//                dto.getChatId().equals(987654321L) &&
//                dto.getProductName().equals("Another Product")
//        ));
//    }
}
