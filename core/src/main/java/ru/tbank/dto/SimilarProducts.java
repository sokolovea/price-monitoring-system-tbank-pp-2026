package ru.tbank.dto;

import java.util.List;
import lombok.Data;

@Data
public class SimilarProducts {
    private final List<ProductInfo> products;
}
