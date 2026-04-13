package ru.tbank.pp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tbank.pp.client.BackendClient;
import ru.tbank.pp.properties.TgBotProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты TgBotService")
class TgBotServiceTest {

    @Mock
    private BackendClient backendClient;

    @Test
    @DisplayName("should создать сервис и вернуть имя бота")
    void constructor_shouldCreateServiceAndReturnBotName() throws TelegramApiException {
        // given
        TgBotProperties tgBotProperties = mock(TgBotProperties.class);
        when(tgBotProperties.getToken()).thenReturn("test-token");
        when(tgBotProperties.getName()).thenReturn("my-test-bot");

        // when
        TgBotService service = new TgBotService(tgBotProperties, backendClient);

        // then
        assertThat(service.getBotUsername()).isEqualTo("my-test-bot");
        verify(tgBotProperties).getToken();
        verify(tgBotProperties).getName();
    }

    @Test
    @DisplayName("should создать сервис с разными параметрами")
    void constructor_shouldCreateServiceWithDifferentParams() throws TelegramApiException {
        // given
        TgBotProperties tgBotProperties = mock(TgBotProperties.class);
        when(tgBotProperties.getToken()).thenReturn("another-token");
        when(tgBotProperties.getName()).thenReturn("another-bot");

        // when
        TgBotService service = new TgBotService(tgBotProperties, backendClient);

        // then
        assertThat(service.getBotUsername()).isEqualTo("another-bot");
    }
}
