package ru.tbank.pp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.model.ProductsNotification;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.model.ProductsProductDetail;
import ru.tbank.pp.service.ProductPriceService;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductPriceService productPriceService;

    public ProductsProduct toProductsProduct(Product product) {
        var productsProduct = new ProductsProduct();
        productsProduct.setId(product.getId());
        productsProduct.setName(product.getName());
        productsProduct.setBrand(product.getBrand());
        productsProduct.setCurrentPrice(productPriceService.getCurrentPrice(product.getId()));
        productsProduct.setImage(product.getImage());
        productsProduct.setLastChecked(OffsetDateTime.now());
        productsProduct.setMarketplace(product.getMarketplace());
        productsProduct.setNmId(product.getId());
        productsProduct.setUrl(product.getUrl());
        return productsProduct;
    }

    public ProductsProductDetail toProductsProductDetail(Product product) {
        var productsProductDetail = new ProductsProductDetail();
        productsProductDetail.setId(product.getId());
        productsProductDetail.setName(product.getName());
        productsProductDetail.setBrand(product.getBrand());
        productsProductDetail.setCurrentPrice(productPriceService.getCurrentPrice(product.getId()));
        productsProductDetail.setImage(product.getImage());
        productsProductDetail.setLastChecked(OffsetDateTime.now());
        productsProductDetail.setMarketplace(product.getMarketplace());
        productsProductDetail.setNmId(product.getId());
        productsProductDetail.setUrl(product.getUrl());
        return productsProductDetail;
    }
}
