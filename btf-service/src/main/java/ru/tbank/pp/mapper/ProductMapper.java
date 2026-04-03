package ru.tbank.pp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.model.ProductsMarketplace;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.service.ProductPriceService;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductPriceService productPriceService;

    public ProductsProduct toProductsProduct(Product product) {
        ProductsProduct productsProduct = new ProductsProduct();
        productsProduct.setId(product.getId());
        productsProduct.setName(product.getName());
        productsProduct.setBrand(product.getBrand());
        productsProduct.setCurrentPrice(productPriceService.getCurrentPrice(product.getId()));
        productsProduct.setImage(product.getImage());
        productsProduct.setLastChecked(OffsetDateTime.now());
        productsProduct.setMarketplace(ProductsMarketplace.WILDBERRIES);//todo исправить
        productsProduct.setNmId(product.getId());
        return productsProduct;
    }
}
