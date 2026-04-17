package ru.tbank.pp.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.dto.UpdatePriceResponseList;
import ru.tbank.pp.service.ProductPriceService;
import ru.tbank.pp.service.ProductService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductResponseConsumer {
    private final ProductPriceService productPriceService;
    private static final String GROUP = "product-group";
    private static final String TOPIC = "product-update-response";

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP,
            containerFactory = "kafkaListenerFactory"
    )
    public void consume(UpdatePriceResponseList updatePriceResponseList) {
        var updatePriceResponse = updatePriceResponseList.getResponses().getLast();
        log.info("Consumer received update product \nID: {}, \nPrice: {}, \nDate: {}",
                updatePriceResponse.getId(),
                updatePriceResponse.getPrice(),
                updatePriceResponse.getDate()
        );
        productPriceService.setProductPrice(updatePriceResponse);
    }
}
