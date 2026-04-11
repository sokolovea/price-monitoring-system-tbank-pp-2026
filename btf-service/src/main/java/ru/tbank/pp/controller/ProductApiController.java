package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.ProductsApi;
import ru.tbank.pp.model.*;
import ru.tbank.pp.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductApiController implements ProductsApi {
    private final ProductService productService;

    @Override
    public ResponseEntity<List<ProductsProductForUpdate>> productsGetProductsForUpdate() {
        return ProductsApi.super.productsGetProductsForUpdate();
    }

    @Override
    public ResponseEntity<ProductsProduct> productsAddProduct(ProductsUrl productsUrl) {
        return ResponseEntity.of(Optional.of(productService.addProduct(productsUrl)));
    }

    @Override
    public ResponseEntity<Void> productsDeleteProduct(Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ProductsProductDetail> productsGetProductById(Long productId) {
        return ResponseEntity.of(Optional.of(productService.getProductDetail(productId)));
    }

    @Override
    public ResponseEntity<List<ProductsProduct>> productsGetProducts() {
        return ResponseEntity.of(Optional.of(productService.getAllUserProducts()));
    }

    @Override
    public ResponseEntity<ProductsNotification> productsNotificationSubscribe(Long productId, ProductsNotificationUpdate productsNotificationUpdate) {
        return ResponseEntity.of(Optional.of(productService.subscribeNotification(productId, productsNotificationUpdate)));
    }

    @Override
    public ResponseEntity<Boolean> productsNotificationUnSubscribe(Long productId) {
        return ResponseEntity.ok(productService.unsubscribeNotification(productId));
    }

    @Override
    public ResponseEntity<ProductsProductPreview> productsProductPreview(ProductsUrl productUrl) {
        return  ResponseEntity.of(Optional.of(productService.getProductPreview(productUrl)));
    }

    @Override
    public ResponseEntity<List<ProductsProductDetail>> productsProductCompare(ProductsIdList productsIdList) {
        return ResponseEntity.of(Optional.of(productService.getProductDetailList(productsIdList.getIds())));
    }
}
