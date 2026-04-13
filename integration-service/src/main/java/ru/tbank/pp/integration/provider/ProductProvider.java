package ru.tbank.pp.integration.provider;

import java.util.List;
import ru.tbank.dto.NormalizedReference;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.dto.UpdatePriceResponse;

public interface ProductProvider {
    ProductInfo getProductInfo(ProductReference productReference);
    List<ProductInfo> getProductInfo(List<NormalizedReference> productReference);
    List<UpdatePriceResponse> getPriceInfo(List<UpdatePriceRequest> productReference);
    List<ProductInfo> getSimilarProducts(NormalizedReference productReference);
    NormalizedReference normalize(ProductReference productReference);
}
