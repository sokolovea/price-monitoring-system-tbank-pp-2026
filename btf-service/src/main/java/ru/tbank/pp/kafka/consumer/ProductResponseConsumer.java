package ru.tbank.pp.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdatePriceResponseList;
import ru.tbank.pp.service.ProductPriceService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductResponseConsumer {
    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update-response";

    private final ProductPriceService productPriceService;

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdatePriceResponseList updatePriceResponseList) {
        log.info("Consumer received update product list: {}", updatePriceResponseList);
        updatePriceResponseList.getItems()
                .forEach(productPriceService::setProductPrice);
    }
}
