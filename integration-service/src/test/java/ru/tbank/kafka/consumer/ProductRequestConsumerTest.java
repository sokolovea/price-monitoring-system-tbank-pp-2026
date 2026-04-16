package ru.tbank.kafka.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdateProductPriceRequestDto;
import ru.tbank.dto.UpdateProductPriceRequestDtoList;
import ru.tbank.enums.Marketplace;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ProductRequestConsumer")
class ProductRequestConsumerTest {

    @InjectMocks
    private ProductRequestConsumer productRequestConsumer;

    private UpdateProductPriceRequestDtoList testRequestList;

    @BeforeEach
    void setUp() {
        UpdateProductPriceRequestDto firstRequest = new UpdateProductPriceRequestDto();
        firstRequest.setId(1L);
        firstRequest.setMarketplace(Marketplace.Ozon);
        firstRequest.setArticle(100L);
        firstRequest.setOptionId(1L);

        UpdateProductPriceRequestDto secondRequest = new UpdateProductPriceRequestDto();
        secondRequest.setId(2L);
        secondRequest.setMarketplace(Marketplace.Wildberries);
        secondRequest.setArticle(200L);
        secondRequest.setOptionId(2L);

        testRequestList = new UpdateProductPriceRequestDtoList(List.of(firstRequest, secondRequest));
    }

    @Test
    @DisplayName("consume() должен успешно обрабатывать входящие сообщения")
    void consume_shouldProcessIncomingMessages() {
        // given & when & then
        assertDoesNotThrow(() -> productRequestConsumer.consume(testRequestList));
    }

    @Test
    @DisplayName("consume() должен обрабатывать список с одним элементом")
    void consume_shouldProcessSingleItemList() {
        // given
        UpdateProductPriceRequestDto singleRequest = new UpdateProductPriceRequestDto();
        singleRequest.setId(3L);
        singleRequest.setMarketplace(Marketplace.Ozon);
        singleRequest.setArticle(300L);
        singleRequest.setOptionId(3L);

        UpdateProductPriceRequestDtoList singleItemList = new UpdateProductPriceRequestDtoList(List.of(singleRequest));

        // when & then
        assertDoesNotThrow(() -> productRequestConsumer.consume(singleItemList));
    }

    // TODO: Добавить тесты на проверку логирования после интеграции с логгером
    // TODO: Добавить тесты на обработку невалидных данных
    // TODO: Добавить тесты на обработку пустого списка
}
