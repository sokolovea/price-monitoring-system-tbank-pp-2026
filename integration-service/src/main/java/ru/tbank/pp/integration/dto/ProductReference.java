package ru.tbank.pp.integration.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.tbank.pp.integration.provider.ProviderType;

@Data
@RequiredArgsConstructor
public class ProductReference {
    @NonNull
    ProviderType marketplace;
    String url;
    String sku;
    String optionId;
}
