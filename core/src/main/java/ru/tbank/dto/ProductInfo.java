package ru.tbank.dto;

import lombok.Builder;
import lombok.Data;
import ru.tbank.pp.model.ProductsMarketplace;

@Data
@Builder
public class ProductInfo {
    private String name;
    private String category;
    private String brand;
    private String rating;

    private String url;
    private String sku;
    private ProductsMarketplace marketplace;

    private Long price;
    private String optionId; private String optionName;

    private String imageUrl;
    private String previewUrl;
}
