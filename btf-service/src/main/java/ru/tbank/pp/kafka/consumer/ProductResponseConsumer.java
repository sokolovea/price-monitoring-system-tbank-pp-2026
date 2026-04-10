package ru.tbank.pp.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdateProductPriceResponseDto;

@Slf4j
@Component
public class ProductResponseConsumer {
    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update-response";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdateProductPriceResponseDto updateProductPriceResponseDto) {
        log.info("Consumer received update product \nID: {}, \nPrice: {}, \nDate: {}",
                updateProductPriceResponseDto.getProductId(),
                updateProductPriceResponseDto.getPrice(),
                updateProductPriceResponseDto.getDate()
        );
    }
}
