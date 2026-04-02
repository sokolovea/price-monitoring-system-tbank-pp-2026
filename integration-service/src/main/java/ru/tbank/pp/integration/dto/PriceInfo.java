package ru.tbank.pp.integration.dto;

import java.time.Instant;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Price information for a given product
 */
@Data
@RequiredArgsConstructor
public class PriceInfo {
    /**
     * URL of a given product.
     */
    String productUrl;

    /**
     * Current price in RUB * 100. (i.e. 456.12 rub turns into 45612)
     */
    Long price;

    /**
     * When was this information gathered
     */
    Instant updatedAt;
}
