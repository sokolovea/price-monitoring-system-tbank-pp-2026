package ru.tbank.pp.integration.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.tbank.dto.UpdatePriceResponseList;

@Service
@RequiredArgsConstructor
public class ProductResponseProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    private static final String TOPIC = "product-update-response";

    public void produce(UpdatePriceResponseList updatePriceResponse) {
        kafkaTemplate.send(TOPIC, updatePriceResponse);
    }
}
