package ru.tbank.pp.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.dto.UpdateProductPriceRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRequestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "product-update-request";

    public void produce(List<UpdateProductPriceRequestDto> updateProductPriceRequestDtoList) {
        kafkaTemplate.send(TOPIC, updateProductPriceRequestDtoList);
    }
}
