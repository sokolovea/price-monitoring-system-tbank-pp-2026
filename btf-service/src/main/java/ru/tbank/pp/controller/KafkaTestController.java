package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.dto.UpdateProductPriceRequestDto;
import ru.tbank.pp.kafka.producer.ProductRequestProducer;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {
    private final ProductRequestProducer productProducer;
    @PostMapping
    public void test(@RequestBody List<UpdateProductPriceRequestDto> updateProductPriceRequestDtoList) {
        productProducer.produce(updateProductPriceRequestDtoList);
    }
}