package ru.tbank.pp.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты ApiProperties")
class ApiPropertiesTest {

    @Test
    @DisplayName("should создать properties с baseUrl")
    void properties_shouldCreateWithBaseUrl() {
        // given
        ApiProperties properties = new ApiProperties();
        properties.setBaseUrl("https://api.example.com");

        // when & then
        assertThat(properties.getBaseUrl()).isEqualTo("https://api.example.com");
    }

    @Test
    @DisplayName("should создать properties с null baseUrl")
    void properties_shouldCreateWithNullBaseUrl() {
        // given
        ApiProperties properties = new ApiProperties();

        // when & then
        assertThat(properties.getBaseUrl()).isNull();
    }
}
