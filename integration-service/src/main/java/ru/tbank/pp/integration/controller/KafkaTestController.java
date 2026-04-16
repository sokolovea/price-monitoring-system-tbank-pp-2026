package ru.tbank.pp.integration.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.dto.UpdatePriceResponseList;
import ru.tbank.pp.integration.kafka.producer.ProductResponseProducer;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {
    private final ProductResponseProducer productProducer;
    @PostMapping
    public void test(@RequestBody UpdatePriceResponseList updatePriceResponseList) {
        productProducer.produce(updatePriceResponseList);
    }
}
