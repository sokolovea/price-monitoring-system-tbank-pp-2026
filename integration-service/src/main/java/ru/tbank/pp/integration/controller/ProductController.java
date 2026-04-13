package ru.tbank.pp.integration.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderFactory;
import ru.tbank.pp.integration.provider.UrlParser;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProviderFactory providerFactory;
    private final UrlParser urlParser;

    @PostMapping("/product")
    public ResponseEntity<ProductInfo> getProduct(@RequestBody ProductReference productReference) {
        log.debug("Received get product request. Product reference: {}", productReference);
        urlParser.setProvider(productReference);
        ProductProvider provider = providerFactory.getProvider(productReference.getMarketplace());
        ProductInfo result = provider.getProductInfo(productReference);
        log.debug("Got product info: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/similar")
    public ResponseEntity<List<ProductInfo>> getSimilarProduct(@RequestBody ProductReference productReference) {
        log.debug("Received get similar product request. Product reference: {}", productReference);
        urlParser.setProvider(productReference);
        ProductProvider provider = providerFactory.getProvider(productReference.getMarketplace());
        List<ProductInfo> result = provider.getSimilarProducts(provider.normalize(productReference));
        log.debug("Similar product info: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
