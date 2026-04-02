package ru.tbank.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdateProductPriceRequestDto;

import java.util.List;

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
    public void consume(List<UpdateProductPriceRequestDto> updateProductPriceRequestDtoList) {
        log.info("Consumer received products for update \nID: {}, \nID: {}",
                updateProductPriceRequestDtoList.getFirst().getId(),
                updateProductPriceRequestDtoList.getLast().getId()
        );
    }
}
