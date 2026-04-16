package ru.tbank.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.tbank.pp.model.ProductsMarketplace;

@Data
@RequiredArgsConstructor
public class ProductReference {
    private String url;

    private ProductsMarketplace marketplace;

    private String sku;

    private String optionId;
}
