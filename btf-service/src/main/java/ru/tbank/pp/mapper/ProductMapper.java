package ru.tbank.pp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.model.*;
import ru.tbank.pp.service.ProductPriceService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductPriceService productPriceService;

    public ProductsProduct toProductsProduct(ProductInfo productInfo) {
        var productsProduct = new ProductsProduct();
        //productsProduct.setId(productInfo.getId());
        productsProduct.setName(productInfo.getName());
        productsProduct.setBrand(productInfo.getBrand());
        productsProduct.setCurrentPrice(BigDecimal.valueOf(productInfo.getPrice()));
        productsProduct.setImage(productInfo.getImageUrl());
        productsProduct.setLastChecked(OffsetDateTime.now());
        productsProduct.setMarketplace(ProductsMarketplace.fromValue(productInfo.getMarketplace().toString()));
        productsProduct.setNmId(Long.valueOf(productInfo.getSku()));
        productsProduct.setUrl(productInfo.getUrl());
        return productsProduct;
    }

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

    public ProductsProductPreview toProductsProductPreview(Product product) {
        var productsProductPreview = new ProductsProductPreview();
        productsProductPreview.setCurrentPrice(productPriceService.getCurrentPrice(product.getId()));
        productsProductPreview.setImage(product.getImage());
        productsProductPreview.setTitle(product.getName());
        return productsProductPreview;
    }

    public Product toProduct(ProductInfo createProductDto) {
        var product = new Product();
        product.setName(createProductDto.getName());
        product.setBrand(createProductDto.getBrand());
        product.setArticle(createProductDto.getSku());
        product.setMarketplace(ProductsMarketplace.fromValue(createProductDto.getMarketplace().toString()));
        product.setUrl(createProductDto.getUrl());
        product.setOptionId(createProductDto.getOptionId());
        product.setOptionName(createProductDto.getOptionName());
        product.setImage(createProductDto.getImageUrl());
        product.setIsTracked(false);

        return product;
    }

    public ProductReference toProductReference(Product product) {
        var productReference = new ProductReference();
        productReference.setMarketplace(product.getMarketplace());
        productReference.setUrl(product.getUrl());
        productReference.setSku(product.getArticle());
        productReference.setOptionId(product.getOptionId());
        return productReference;
    }
}
