package ru.tbank.pp.integration.provider.wildberries;

import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;

public class WildberriesProvider implements ProductProvider {
    @Override
    public ProductInfo getProductInfo(ProductReference productReference) {
        return null;
    }

    @Override
    public PriceInfo getPriceInfo(ProductReference productReference) {
        return null;
    }
}
