package ru.tbank.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePriceResponse {
    private Long id;
    private BigDecimal price;
    private Instant date;
}
