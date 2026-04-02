package ru.tbank.pp.integration.provider;

import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;

public interface ProductProvider {
    ProductInfo getProductInfo(ProductReference productReference);
    PriceInfo getPriceInfo(ProductReference productReference);
}
