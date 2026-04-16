package ru.tbank.pp.integration.provider.wildberries.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceSchema {
    Long basic;
    Long product;
}
