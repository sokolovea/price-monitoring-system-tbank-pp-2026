package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import ru.tbank.pp.api.ProductsApi;
import ru.tbank.pp.model.ProductsNotification;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.model.ProductsProductForUpdate;
import ru.tbank.pp.model.ProductsProductPreview;
import ru.tbank.pp.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductApiController implements ProductsApi {
    private final ProductService productService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ProductsApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<ProductsProductForUpdate>> productsGetProductsForUpdate() {
        return ProductsApi.super.productsGetProductsForUpdate();
    }

    @Override
    public ResponseEntity<ProductsProduct> productsAddProduct(String body) {
        return ProductsApi.super.productsAddProduct(body);
    }

    @Override
    public ResponseEntity<Void> productsDeleteProduct(Long productId) {
        return ProductsApi.super.productsDeleteProduct(productId);
    }

    @Override
    public ResponseEntity<ProductsProduct> productsGetProductById(Long productId) {
        return ProductsApi.super.productsGetProductById(productId);
    }

    @Override
    public ResponseEntity<List<ProductsProduct>> productsGetProducts() {
        return ResponseEntity.of(Optional.ofNullable(productService.getAllUserProducts()));
    }

    @Override
    public ResponseEntity<ProductsNotification> productsNotificationSubscribe(Long productId, ProductsNotification productsNotification) {
        return ProductsApi.super.productsNotificationSubscribe(productId, productsNotification);
    }

    @Override
    public ResponseEntity<Boolean> productsNotificationUnSubscribe(Long productId) {
        return ProductsApi.super.productsNotificationUnSubscribe(productId);
    }

    @Override
    public ResponseEntity<ProductsProductPreview> productsProductPreview(String body) {
        return ProductsApi.super.productsProductPreview(body);
    }
}
