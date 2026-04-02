package ru.tbank.pp.integration.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderFactory;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProviderFactory providerFactory;

    @PostMapping("/product")
    public ProductInfo getProduct(@RequestBody ProductReference productReference) {
        log.debug("Received product reference: {}", productReference);
        ProductProvider provider = providerFactory.getProvider(productReference.getMarketplace());
        ProductInfo result = provider.getProductInfo(productReference);
        log.debug("Got product info: {}", result);
        return result;
    }
}
