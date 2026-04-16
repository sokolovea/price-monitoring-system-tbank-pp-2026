package ru.tbank.dto;

import lombok.Getter;
import lombok.Setter;
import ru.tbank.pp.model.ProductsMarketplace;

@Getter
@Setter
public class UpdatePriceRequest implements HasSku {
    private Long id;
    private ProductsMarketplace marketplace;
    private String sku;
    private String optionId;
}