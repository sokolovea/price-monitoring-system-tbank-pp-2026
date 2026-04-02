package ru.tbank.pp.integration.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.tbank.pp.integration.provider.ProviderType;

@Data
@RequiredArgsConstructor
public class ProductReference {
    String url;

    @NonNull
    ProviderType marketplace;

    String sku;
}
