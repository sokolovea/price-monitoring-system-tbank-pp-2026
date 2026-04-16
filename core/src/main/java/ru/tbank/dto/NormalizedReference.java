package ru.tbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.tbank.pp.model.ProductsMarketplace;

@Data
@AllArgsConstructor
public class NormalizedReference implements HasSku {
    @NonNull
    String sku;

    @NonNull
    ProductsMarketplace marketplace;

    String optionId;
}
