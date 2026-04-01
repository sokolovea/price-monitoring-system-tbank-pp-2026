package ru.tbank.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.dto.UpdateProductDto;

@Service
@RequiredArgsConstructor
public class ProductProducer {
    private final KafkaTemplate<String, UpdateProductDto> kafkaTemplate;

    private static final String TOPIC = "product-update";

    public void produce(UpdateProductDto updateProductDto) {
        kafkaTemplate.send(TOPIC, updateProductDto);
    }
}
