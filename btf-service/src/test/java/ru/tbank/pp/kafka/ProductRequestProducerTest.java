package ru.tbank.pp.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.tbank.dto.UpdateProductPriceRequestDto;
import ru.tbank.dto.UpdateProductPriceRequestDtoList;
import ru.tbank.enums.Marketplace;
import ru.tbank.pp.kafka.producer.ProductRequestProducer;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRequestProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ProductRequestProducer productRequestProducer;

    @Test
    void produce_Success() {
        UpdateProductPriceRequestDto dto = new UpdateProductPriceRequestDto();
        dto.setId(1L);
        dto.setMarketplace(Marketplace.Ozon);
        dto.setArticle(12345L);
        dto.setOptionId(100L);

        UpdateProductPriceRequestDtoList dtoList = new UpdateProductPriceRequestDtoList(List.of(dto));

        productRequestProducer.produce(dtoList);

        verify(kafkaTemplate).send("product-update-request", dtoList);
    }
}
