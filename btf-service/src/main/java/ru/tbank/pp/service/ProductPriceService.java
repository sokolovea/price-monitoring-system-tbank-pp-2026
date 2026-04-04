package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.repository.ProductPriceRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductPriceRepository productPriceRepository;

    public BigDecimal getCurrentPrice(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId).getFirst().getPrice();
    }

    public List<ProductPrice> getProductPrices(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId);
    }
}
