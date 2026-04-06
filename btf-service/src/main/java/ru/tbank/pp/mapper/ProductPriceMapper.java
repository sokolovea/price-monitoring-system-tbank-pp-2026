package ru.tbank.pp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.dto.UpdateProductPriceResponseDto;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.model.ProductsPriceHistory;
import ru.tbank.pp.service.ProductService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ProductPriceMapper {
    private final ProductService productService;
    public ProductsPriceHistory mapToProductsPriceHistory(ProductPrice productPrice) {
        var productPriceHistory = new ProductsPriceHistory();
        productPriceHistory.setPrice(productPrice.getPrice());
        productPriceHistory.setDate(productPrice.getId().getCheckDate().atOffset(ZoneOffset.UTC));
        return productPriceHistory;
    }

    public ProductPrice toProductPrice(UpdateProductPriceResponseDto updateProductPriceResponseDto) {
        var productPriceId = new ProductPriceId();
        productPriceId.setProductId(updateProductPriceResponseDto.getProductId());
        productPriceId.setCheckDate(updateProductPriceResponseDto.getDate());

        var productPrice = new ProductPrice();
        productPrice.setId(productPriceId);
        productPrice.setProduct(productService.getProduct(productPriceId.getProductId()));
        productPrice.setPrice(updateProductPriceResponseDto.getPrice());

        return productPrice;
    }
}
