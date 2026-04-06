package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.dto.UpdateProductPriceRequestDto;
import ru.tbank.dto.UpdateProductPriceResponseDto;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.repository.ProductPriceRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductPriceRepository productPriceRepository;

    private final ProductPriceMapper productPriceMapper;

    public BigDecimal getCurrentPrice(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId).getFirst().getPrice();
    }

    public List<ProductPrice> getProductPrices(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId);
    }

    public void setProductPrice(UpdateProductPriceResponseDto updateProductPriceResponseDto) {
        var prodcutPrice = productPriceMapper.toProductPrice(updateProductPriceResponseDto);
        productPriceRepository.save(prodcutPrice);
    }
}
