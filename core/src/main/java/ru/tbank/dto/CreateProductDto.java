package ru.tbank.dto;

import lombok.*;

import java.math.BigDecimal;
import ru.tbank.pp.model.ProductsMarketplace;

@Getter
@Setter
public class CreateProductDto {
    private String brand;
    private String category;
    private String imageUrl;
    private ProductsMarketplace marketplace;
    private String name;
    private Long optionId;
    private String optionName;
    private String previewUrl;
    private BigDecimal price;
    private String rating;
    private Long sku;
    private String url;
}
