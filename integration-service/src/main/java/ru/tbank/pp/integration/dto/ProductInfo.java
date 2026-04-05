package ru.tbank.pp.integration.dto;

import lombok.Builder;
import lombok.Data;
import ru.tbank.pp.integration.provider.ProviderType;

@Data
@Builder
public class ProductInfo {
    String name;
    String category;
    String brand;
    String rating;

    String url;
    String sku;
    ProviderType marketplace;

    Long price;
    String optionId;
    String optionName;

    String imageUrl;
    String previewUrl;
}
