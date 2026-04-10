package ru.tbank.pp.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.tbank.pp.integration.provider.ProviderType;

@Data
@AllArgsConstructor
public class NormalizedReference {
    @NonNull
    String sku;

    @NonNull
    ProviderType marketplace;

    String optionId;
}
