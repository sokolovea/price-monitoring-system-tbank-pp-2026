package ru.tbank.pp.integration.provider.wildberries.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SizeSchema {
    String origName;
    Long optionId;
    PriceSchema price;
}
