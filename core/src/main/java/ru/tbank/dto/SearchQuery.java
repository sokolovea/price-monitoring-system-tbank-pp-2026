package ru.tbank.dto;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.tbank.pp.model.ProductsMarketplace;

@Data
@RequiredArgsConstructor
public class SearchQuery {
    @NonNull
    private String query;

    @NonNull
    private ProductsMarketplace marketplace;

    @NonNull
    private Long limit;

    @NonNull
    private Long offset;
}
