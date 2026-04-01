package ru.tbank.pp.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdateProductDto;

@Slf4j
@Component
public class ProductConsumer {
    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdateProductDto updateProductDto) {
        log.info("Consumer received update product \nID: {}, \nPrice: {}, \nDate: {}",
                updateProductDto.getId(),
                updateProductDto.getPrice(),
                updateProductDto.getDate()
        );
    }
}
