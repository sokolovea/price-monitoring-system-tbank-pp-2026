package ru.tbank.pp.integration.provider;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.tbank.dto.ProductReference;
import ru.tbank.pp.integration.provider.exception.UnsupportedProviderException;
import ru.tbank.pp.model.ProductsMarketplace;

@Slf4j
@Component
public class UrlParser {
    private final Map<String, ProductsMarketplace> hostToMarketplace = new HashMap<>();

    @PostConstruct
    public void init() {
        hostToMarketplace.put("www.wildberries.ru", ProductsMarketplace.WILDBERRIES);

        hostToMarketplace.put("www.ozon.ru", ProductsMarketplace.OZON);
        hostToMarketplace.put("www.ozon.by", ProductsMarketplace.OZON);
        hostToMarketplace.put("www.ozon.kz", ProductsMarketplace.OZON);
    }

    public void setProvider(ProductReference productReference) {
        UriComponents uri = UriComponentsBuilder.fromUriString(productReference.getUrl()).build();
        ProductsMarketplace marketplace = hostToMarketplace.getOrDefault(
                uri.getHost(),
                null
        );
        if (marketplace == null) {
            log.debug("No marketplace found for url: {}", productReference.getUrl());
            throw new UnsupportedProviderException("Unsupported url: " +  productReference.getUrl());
        }
        productReference.setMarketplace(marketplace);
    }
}
