package ru.tbank.pp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.properties.ApiProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Тесты RestClientConfig")
class RestClientConfigTest {

    @Test
    @DisplayName("should создать RestClient с правильным baseUrl")
    void restClient_shouldCreateWithCorrectBaseUrl() {
        // given
        ApiProperties apiProperties = mock(ApiProperties.class);
        when(apiProperties.getBaseUrl()).thenReturn("https://api.example.com");
        
        RestClientConfig config = new RestClientConfig(apiProperties);

        // when
        RestClient restClient = config.restClient();

        // then
        assertThat(restClient).isNotNull();
        verify(apiProperties).getBaseUrl();
    }

    @Test
    @DisplayName("should создать RestClient с null baseUrl")
    void restClient_shouldCreateWithNullBaseUrl() {
        // given
        ApiProperties apiProperties = mock(ApiProperties.class);
        when(apiProperties.getBaseUrl()).thenReturn(null);
        
        RestClientConfig config = new RestClientConfig(apiProperties);

        // when
        RestClient restClient = config.restClient();

        // then
        assertThat(restClient).isNotNull();
    }
}
