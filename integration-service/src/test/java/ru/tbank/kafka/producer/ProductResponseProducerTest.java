package ru.tbank.kafka.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdateProductPriceResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ProductResponseProducer")
class ProductResponseProducerTest {

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ProductResponseProducer productResponseProducer;

    private UpdateProductPriceResponseDto testDto;

    @BeforeEach
    void setUp() {
        testDto = new UpdateProductPriceResponseDto();
        testDto.setProductId(1L);
        testDto.setPrice(BigDecimal.valueOf(100.50));
        testDto.setDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("produce() должен вызывать kafkaTemplate.send() с правильными параметрами")
    void produce_shouldSendToCorrectTopic() {
        // given
        String expectedTopic = "product-update-response";

        // when
        productResponseProducer.produce(testDto);

        // then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UpdateProductPriceResponseDto> messageCaptor = ArgumentCaptor.forClass(UpdateProductPriceResponseDto.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(expectedTopic);
        assertThat(messageCaptor.getValue()).isEqualTo(testDto);
    }

    @Test
    @DisplayName("produce() должен отправлять разные сообщения независимо")
    void produce_shouldSendMultipleMessagesIndependently() {
        // given
        UpdateProductPriceResponseDto firstDto = new UpdateProductPriceResponseDto();
        firstDto.setProductId(2L);
        firstDto.setPrice(BigDecimal.valueOf(200.00));
        firstDto.setDate(LocalDateTime.now());

        UpdateProductPriceResponseDto secondDto = new UpdateProductPriceResponseDto();
        secondDto.setProductId(3L);
        secondDto.setPrice(BigDecimal.valueOf(300.00));
        secondDto.setDate(LocalDateTime.now());

        // when
        productResponseProducer.produce(firstDto);
        productResponseProducer.produce(secondDto);

        // then
        verify(kafkaTemplate, times(2)).send(anyString(), any(UpdateProductPriceResponseDto.class));
    }
}
