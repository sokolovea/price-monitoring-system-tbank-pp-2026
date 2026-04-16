package ru.tbank.pp.integration.provider.wildberries.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSchema {
    Long id;
    String name;
    String brand;
    String entity;
    Float reviewRating;
    List<SizeSchema> sizes;
}
