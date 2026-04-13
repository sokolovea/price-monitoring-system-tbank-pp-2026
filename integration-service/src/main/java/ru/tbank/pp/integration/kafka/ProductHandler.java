package ru.tbank.pp.integration.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderFactory;
import ru.tbank.pp.model.ProductsMarketplace;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductHandler {
    private final ProviderFactory providerFactory;

//    public List<ProductInfo> getProducts(List<NormalizedReference> productReferences) {
//        Map<ProductsMarketplace, List<NormalizedReference>> productsByProvider = productReferences.stream()
//                .collect(
//                        Collectors.groupingBy(NormalizedReference::getMarketplace)
//                );
//
//        List<ProductInfo> result = new ArrayList<>(productReferences.size());
//        productsByProvider.keySet()
//                .forEach(providerType -> {
//                    ProductProvider provider = providerFactory.getProvider(providerType);
//                    result.addAll(provider.getProductInfo(productsByProvider.get(providerType)));
//                });
//        return result;
//    }

    public List<UpdatePriceResponse> getPrices(List<UpdatePriceRequest> productReferences) {
        Map<ProductsMarketplace, List<UpdatePriceRequest>> productsByProvider = productReferences.stream()
                .collect(Collectors.groupingBy(UpdatePriceRequest::getMarketplace));

        List<UpdatePriceResponse> result = new ArrayList<>(productReferences.size());
        productsByProvider.keySet()
                .forEach(providerType -> {
                    ProductProvider provider = providerFactory.getProvider(providerType);
                    result.addAll(provider.getPriceInfo(productsByProvider.get(providerType)));
                });
        return result;
    }
}
