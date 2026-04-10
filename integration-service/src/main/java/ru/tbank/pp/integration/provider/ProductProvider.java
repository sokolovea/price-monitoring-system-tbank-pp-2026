package ru.tbank.pp.integration.provider;

import java.util.List;
import ru.tbank.pp.integration.dto.NormalizedReference;
import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;

public interface ProductProvider {
    ProductInfo getProductInfo(ProductReference productReference);
    List<ProductInfo> getProductInfo(List<NormalizedReference> productReference);
    List<PriceInfo> getPriceInfo(List<NormalizedReference> productReference);

    List<ProductInfo> getSimilarProducts(NormalizedReference productReference);
    List<ProductInfo> searchProducts(String query);

    NormalizedReference normalize(ProductReference productReference);
}
