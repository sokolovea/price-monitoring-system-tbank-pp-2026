package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;
import ru.tbank.enums.Marketplace;

@Getter
@Setter
public class UpdateProductPriceRequestDto {
    private Long id;
    private Marketplace marketplace;
    private Long article;
    private Long optionId;
}
