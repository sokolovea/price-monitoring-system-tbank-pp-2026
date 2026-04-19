package ru.tbank.pp.integration.provider;

import java.util.List;
import ru.tbank.dto.NormalizedReference;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.dto.SearchQuery;
import ru.tbank.dto.SimilarProducts;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.model.ProductsUrl;

public interface ProductProvider {
    ProductInfo getProductInfo(ProductReference productReference);
    List<ProductInfo> getProductInfo(List<NormalizedReference> productReference);
    List<UpdatePriceResponse> getPriceInfo(List<UpdatePriceRequest> productReference);
    SimilarProducts getSimilarProducts(NormalizedReference productReference);
    SimilarProducts search(SearchQuery query);
    NormalizedReference normalize(ProductReference productReference);
    ProductReference parseUrl(ProductReference url);
}
