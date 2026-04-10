package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateProductPriceResponseDto {
    private Long productId;
    private BigDecimal price;
    private LocalDateTime date;
}
