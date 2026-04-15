package ru.tbank.pp.mapper;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.model.ProductsPriceHistory;
import ru.tbank.pp.repository.ProductRepository;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ProductPriceMapper {
    private final ProductRepository productRepository;
    public ProductsPriceHistory mapToProductsPriceHistory(ProductPrice productPrice) {
        var productPriceHistory = new ProductsPriceHistory();
        productPriceHistory.setPrice(productPrice.getPrice());
        productPriceHistory.setDate(productPrice.getId().getCheckDate().atOffset(ZoneOffset.UTC));
        return productPriceHistory;
    }

    public ProductPrice toProductPrice(UpdatePriceResponse updatePriceResponse) {
        var productPriceId = new ProductPriceId();
        productPriceId.setProductId(updatePriceResponse.getId());
        //todo
        productPriceId.setCheckDate(LocalDateTime.from(updatePriceResponse.getDate()));

        var product = productRepository.findById(productPriceId.getProductId()).orElseThrow(
                () -> new ProductNotFoundException(
                        String.format("Product with id '%s' not found", productPriceId.getProductId())
                )
        );

        var productPrice = new ProductPrice();
        productPrice.setId(productPriceId);
        productPrice.setProduct(product);
        productPrice.setPrice(updatePriceResponse.getPrice());

        return productPrice;
    }
}
