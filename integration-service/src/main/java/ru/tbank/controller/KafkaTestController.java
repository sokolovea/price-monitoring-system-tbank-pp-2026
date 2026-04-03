package ru.tbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.UpdateProductPriceResponseDto;
import ru.tbank.kafka.producer.ProductResponseProducer;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {
    private final ProductResponseProducer productProducer;
    @PostMapping
    public void test(@RequestBody UpdateProductPriceResponseDto updateProductPriceResponseDto) {
        productProducer.produce(updateProductPriceResponseDto);
    }
}
