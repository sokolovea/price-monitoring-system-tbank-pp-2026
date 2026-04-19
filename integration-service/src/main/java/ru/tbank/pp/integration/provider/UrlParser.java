package ru.tbank.pp.integration.provider;

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
    private final Map<String, ProductsMarketplace> hostToMarketplace = Map.of(
            "wildberries.ru", ProductsMarketplace.WILDBERRIES,
            "ozon.ru", ProductsMarketplace.OZON,
            "ozon.by", ProductsMarketplace.OZON,
            "ozon.kz", ProductsMarketplace.OZON
    );

    private ProductsMarketplace resolveMarketplace(String host) {
        host = host.toLowerCase();
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        return hostToMarketplace.getOrDefault(
                host,
                null
        );
    }

    public void setProvider(ProductReference productReference) {
        if (productReference.getUrl() == null) {
            if (productReference.getMarketplace() != null)
                return;
            else
                throw new UnsupportedProviderException("Marketplace not found");
        }

        UriComponents uri = UriComponentsBuilder.fromUriString(productReference.getUrl()).build();
        ProductsMarketplace marketplace = resolveMarketplace(uri.getHost());
        if (marketplace == null) {
            log.debug("No marketplace found for url: {}", productReference.getUrl());
            throw new UnsupportedProviderException("Unsupported url: " +  productReference.getUrl());
        }
        productReference.setMarketplace(marketplace);
    }
}
