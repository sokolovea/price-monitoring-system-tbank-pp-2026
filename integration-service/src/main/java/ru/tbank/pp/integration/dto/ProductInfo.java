package ru.tbank.pp.integration.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.tbank.pp.integration.provider.ProviderType;

@Data
@RequiredArgsConstructor
public class ProductInfo {
    String name;
    String description;
    String brand;

    String url;
    String sku;
    ProviderType marketplace;

    Long price;
    String optionId;
    String optionName;

    String imageUrl;
    String previewUrl;
}
