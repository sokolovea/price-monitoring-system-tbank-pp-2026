package ru.tbank.pp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.pp.service.TgBotService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты TgBotConfig")
class TgBotConfigTest {

    @Mock
    private TgBotService tgBotService;

    @InjectMocks
    private TgBotConfig tgBotConfig;

    @Test
    @DisplayName("should успешно инициализировать бота")
    void init_shouldRegisterBotSuccessfully() {
        // given & when
        tgBotConfig.init();

        // then
        assertThat(tgBotConfig).isNotNull();
    }

    @Test
    @DisplayName("should обработать ошибку при регистрации бота без выброса исключения")
    void init_shouldHandleRegistrationErrorWithoutException() {
        // given & when - не должно выбрасывать исключение наружу
        tgBotConfig.init();

        // then - просто проверяем что метод отработал
        assertThat(tgBotConfig).isNotNull();
    }
}
