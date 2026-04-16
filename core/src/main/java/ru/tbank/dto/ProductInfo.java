package ru.tbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.pp.model.ProductsMarketplace;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {
    private String name;
    private String category;
    private String brand;
    private String rating;

    private String url;
    private String sku;
    private ProductsMarketplace marketplace;

    private Long price;
    private String optionId;
    private String optionName;

    private String imageUrl;
    private String previewUrl;
}
