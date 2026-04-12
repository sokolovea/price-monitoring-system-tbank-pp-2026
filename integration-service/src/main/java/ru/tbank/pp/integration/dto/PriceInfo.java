package ru.tbank.pp.integration.dto;

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
    String sku;

    /**
     * Product option id
     */
    String optionId;

    /**
     * Current price in RUB * 100. (i.e. 456.12 rub turns into 45612)
     */
    Long price;

    /**
     * When was this information gathered
     */
    Instant lastUpdate;
}
