package ru.tbank.dto;

import java.time.Instant;
import lombok.Data;

/**
 * Price information for a given product
 */
@Data
public class PriceInfo {
    /**
     * Product sku
     */
    private String sku;

    /**
     * Product option id
     */
    private String optionId;

    /**
     * Current price in RUB * 100. (i.e. 456.12 rub turns into 45612)
     */
    private Long price;

    /**
     * When was this information gathered
     */
    private Instant lastUpdate;
}
