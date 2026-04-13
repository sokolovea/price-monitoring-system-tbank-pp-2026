package ru.tbank.pp.integration.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdatePriceRequestList;
import ru.tbank.pp.integration.kafka.ProductHandler;
import ru.tbank.pp.integration.kafka.producer.ProductResponseProducer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRequestConsumer {
    ProductHandler productHandler;
    ProductResponseProducer responseProducer;

    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update-request";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdatePriceRequestList updatePriceRequestList) {
        log.info("Consumer received products for update \nID: {}, \nID: {}",
                updatePriceRequestList.getItems().getFirst().getId(),
                updatePriceRequestList.getItems().getLast().getId()
        );

        responseProducer.produce(
                productHandler.getPrices(updatePriceRequestList.getItems())
        );
    }
}
