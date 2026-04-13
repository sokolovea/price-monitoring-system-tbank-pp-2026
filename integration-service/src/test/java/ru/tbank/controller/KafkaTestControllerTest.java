package ru.tbank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdateProductPriceResponseDto;
import ru.tbank.kafka.producer.ProductResponseProducer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты KafkaTestController")
class KafkaTestControllerTest {

    @Mock
    private ProductResponseProducer productResponseProducer;

    @InjectMocks
    private KafkaTestController kafkaTestController;

    private UpdateProductPriceResponseDto testDto;

    @BeforeEach
    void setUp() {
        testDto = new UpdateProductPriceResponseDto();
        testDto.setProductId(1L);
        testDto.setPrice(BigDecimal.valueOf(99.99));
        testDto.setDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("test() должен вызывать producer.produce() с полученным DTO")
    void test_shouldCallProduceWithDto() {
        // when
        kafkaTestController.test(testDto);

        // then
        verify(productResponseProducer, times(1)).produce(testDto);
    }

    @Test
    @DisplayName("test() должен корректно обрабатывать несколько вызовов")
    void test_shouldHandleMultipleCalls() {
        // given
        UpdateProductPriceResponseDto secondDto = new UpdateProductPriceResponseDto();
        secondDto.setProductId(2L);
        secondDto.setPrice(BigDecimal.valueOf(199.99));
        secondDto.setDate(LocalDateTime.now());

        // when
        kafkaTestController.test(testDto);
        kafkaTestController.test(secondDto);

        // then
        verify(productResponseProducer, times(2)).produce(any(UpdateProductPriceResponseDto.class));
    }

    // TODO: Добавить интеграционные тесты с MockMvc для HTTP уровня
    // TODO: Добавить тесты на валидацию входных данных
    // TODO: Добавить тесты на обработку ошибок
}
