package ru.tbank.pp.kafka.cron;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tbank.dto.UpdatePriceRequestList;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.kafka.producer.ProductRequestProducer;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.service.ProductPriceService;

@Service
@RequiredArgsConstructor
public class PriceUpdater {
    private final ProductPriceService productPriceService;
    private final ProductRequestProducer productRequestProducer;
    private final ProductMapper productMapper;

    @Value("${scheduler.delay}")
    private String delayString;

    private static final int messageLength = 99;

    private long delay;

    @PostConstruct
    private void init() {
        delay = Duration.parse(delayString).toMillis();
    }

    @Scheduled(
            fixedDelayString = "${scheduler.delay}",
            initialDelayString = "${scheduler.initialDelay}"
    )
    public void updatePrice() {
        List<ProductPrice> prices = productPriceService.getLatestPricesUpdatedBefore(
                Instant.now().minus(delay, ChronoUnit.MILLIS)
        );

        int startIdx;
        for (startIdx = 0; startIdx + messageLength < prices.size(); startIdx+=messageLength) {
            var subList = prices.subList(startIdx, startIdx + messageLength);
            var request = new UpdatePriceRequestList(
                    subList.stream()
                            .map(ProductPrice::getProduct)
                            .map(productMapper::toUpdatePriceRequest)
                            .collect(Collectors.toList())
            );
            productRequestProducer.produce(request);
        }

        if (startIdx < prices.size()) {
            var subList = prices.subList(startIdx, prices.size());
            var request = new UpdatePriceRequestList(
                    subList.stream()
                            .map(ProductPrice::getProduct)
                            .map(productMapper::toUpdatePriceRequest)
                            .collect(Collectors.toList())
            );
            productRequestProducer.produce(request);
        } }
}
