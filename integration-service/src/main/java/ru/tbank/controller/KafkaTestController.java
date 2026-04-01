package ru.tbank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.UpdateProductDto;
import ru.tbank.kafka.producer.ProductProducer;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {
    private final ProductProducer productProducer;
    @PostMapping
    public void test(@RequestBody UpdateProductDto updateProductDto) {
        productProducer.produce(updateProductDto);
    }
}
