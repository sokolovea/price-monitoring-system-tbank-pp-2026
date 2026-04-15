package ru.tbank.pp.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты TgBotProperties")
class TgBotPropertiesTest {

    @Test
    @DisplayName("should создать properties с правильными значениями")
    void properties_shouldCreateWithCorrectValues() {
        // given
        TgBotProperties properties = new TgBotProperties();
        properties.setBaseUrl("https://api.telegram.org");
        properties.setWebhookPath("/webhook");
        properties.setName("test-bot");
        properties.setToken("test-token");

        // when & then
        assertThat(properties.getBaseUrl()).isEqualTo("https://api.telegram.org");
        assertThat(properties.getWebhookPath()).isEqualTo("/webhook");
        assertThat(properties.getName()).isEqualTo("test-bot");
        assertThat(properties.getToken()).isEqualTo("test-token");
    }

    @Test
    @DisplayName("should создать properties с null значениями")
    void properties_shouldCreateWithNullValues() {
        // given
        TgBotProperties properties = new TgBotProperties();

        // when & then
        assertThat(properties.getBaseUrl()).isNull();
        assertThat(properties.getWebhookPath()).isNull();
        assertThat(properties.getName()).isNull();
        assertThat(properties.getToken()).isNull();
    }
}
