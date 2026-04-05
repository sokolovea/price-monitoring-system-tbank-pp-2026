package ru.tbank.pp.integration.provider;

import java.util.List;
import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;

public interface ProductProvider {
    ProductInfo getProductInfo(ProductReference productReference);
    List<ProductInfo> getProductInfoList(List<ProductReference> productReference);
    List<PriceInfo> getPriceInfo(List<ProductReference> productReference);
}
