package ru.tbank.pp.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.tbank.dto.UpdatePriceRequestList;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.pp.kafka.producer.ProductRequestProducer;
import ru.tbank.pp.model.ProductsMarketplace;

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
        UpdatePriceRequest dto = new UpdatePriceRequest();
        dto.setId(1L);
        dto.setMarketplace(ProductsMarketplace.OZON);
        dto.setSku("12345");
        dto.setOptionId("100");

        UpdatePriceRequestList dtoList = new UpdatePriceRequestList(List.of(dto));

        productRequestProducer.produce(dtoList);

        verify(kafkaTemplate).send("product-update-request", dtoList);
    }
}
