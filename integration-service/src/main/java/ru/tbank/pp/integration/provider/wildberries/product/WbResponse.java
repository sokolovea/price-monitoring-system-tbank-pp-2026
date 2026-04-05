package ru.tbank.pp.integration.provider.wildberries.product;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WbResponse {
    List<ProductSchema> products;
}
