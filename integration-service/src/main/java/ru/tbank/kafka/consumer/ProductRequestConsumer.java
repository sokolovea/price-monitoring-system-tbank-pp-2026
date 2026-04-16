package ru.tbank.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdateProductPriceRequestDtoList;

@Slf4j
@Component
public class ProductRequestConsumer {
    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update-request";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdateProductPriceRequestDtoList updateProductPriceRequestDtoList) {
        log.info("Consumer received products for update \nID: {}, \nID: {}",
                updateProductPriceRequestDtoList.getItems().getFirst().getId(),
                updateProductPriceRequestDtoList.getItems().getLast().getId()
        );
    }
}
