package ru.tbank.dto;

import lombok.*;
import ru.tbank.enums.Marketplace;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductDto {
    private String brand;
    private String category;
    private String imageUrl;
    private Marketplace marketplace;
    private String name;
    private Long optionId;
    private String optionName;
    private String previewUrl;
    private BigDecimal price;
    private String rating;
    private Long sku;
    private String url;
}
