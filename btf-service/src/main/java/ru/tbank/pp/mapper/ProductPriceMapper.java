package ru.tbank.pp.mapper;

import org.springframework.stereotype.Component;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.model.ProductsPriceHistory;

import java.time.ZoneOffset;

@Component
public class ProductPriceMapper {
    public ProductsPriceHistory mapToProductsPriceHistory(ProductPrice productPrice) {
        var productPriceHistory = new ProductsPriceHistory();
        productPriceHistory.setPrice(productPrice.getPrice());
        productPriceHistory.setDate(productPrice.getId().getCheckDate().atOffset(ZoneOffset.UTC));
        return productPriceHistory;
    }
}
