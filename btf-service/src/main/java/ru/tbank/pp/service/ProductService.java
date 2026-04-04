package ru.tbank.pp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.entity.UserProductId;
import ru.tbank.pp.exception.AuthException;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.mapper.UserProductMapper;
import ru.tbank.pp.model.*;
import ru.tbank.pp.repository.ProductRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;

    private final ProductPriceService productPriceService;

    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;
    private final UserProductMapper userProductMapper;


    public List<ProductsProduct> getAllUserProducts() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        var userId = user.getId();

        var userProducts = userProductRepository.findByUserId(userId);

        return userProducts.stream()
                .map(up -> productMapper.toProductsProduct(up.getProduct()))
                .toList();
    }

    public ProductsProductDetail getProductDetail(Long productId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product with id '%s' not found", productId));
        }

        var product = productOptional.get();
        var productPrices = productPriceService.getProductPrices(productId);
        var priceHistory = productPrices.stream()
                .map(productPriceMapper::mapToProductsPriceHistory)
                .toList();

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(productId);

        var userProductOptional = userProductRepository.findById(userProductId);
        if (userProductOptional.isEmpty()) {
            throw new ProductNotFoundException(
                    String.format("Product with id '%s' not found for user '%s'", productId, user.getId())
            );
        }

        var productsNotification = userProductMapper.toProductsNotification(userProductOptional.get());

        var productDetail = productMapper.toProductsProductDetail(product);
        productDetail.setPriceHistory(priceHistory);
        productDetail.setNotification(productsNotification);

        return productDetail;
    }

    @Transactional
    public ProductsProduct addProduct(String productUrl) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var productOptional = productRepository.findByUrl(productUrl);

        if (productOptional.isEmpty()) {
            //todo добавление первоначальной информации о товаре
        }

        var product = productOptional.get();

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(product.getId());

        var userProduct = new UserProduct();
        userProduct.setId(userProductId);
        userProduct.setProduct(product);
        userProduct.setUser(user);
        userProduct.setNotify(false);

        userProductRepository.save(userProduct);

        return productMapper.toProductsProduct(product);
    }

    public ProductsProductPreview getProductPreview(String productUrl) {
        var productOptional = productRepository.findByUrl(productUrl);

        if (productOptional.isEmpty()) {
            //todo добавление первоначальной информации о товаре
        }

        var product = productOptional.get();

        return productMapper.toProductsProductPreview(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(productId);

        userProductRepository.deleteById(userProductId);
    }

    @Transactional
    public ProductsNotification subscribeNotification(Long productId, ProductsNotificationUpdate productsNotificationUpdate) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(productId);

        var userProductOptional = userProductRepository.findById(userProductId);
        if (userProductOptional.isEmpty()) {
            throw new ProductNotFoundException(
                    String.format("Product with id '%s' not found for user '%s'", productId, user.getId())
            );
        }

        var userProduct = userProductOptional.get();
        userProduct.setNotify(Boolean.TRUE.equals(productsNotificationUpdate.getEnabled()));
        userProduct.setThresholdPrice(productsNotificationUpdate.getThresholdPrice());

        userProductRepository.save(userProduct);

        var productsNotification = new ProductsNotification();
        productsNotification.setThresholdPrice(productsNotificationUpdate.getThresholdPrice());
        productsNotification.setEnabled(Boolean.TRUE.equals(productsNotificationUpdate.getEnabled()));

        return productsNotification;
    }

    @Transactional
    public Boolean unsubscribeNotification(Long productId){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthException("Authentication object is null");
        }
        var user = (User) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(productId);

        var userProductOptional = userProductRepository.findById(userProductId);
        if (userProductOptional.isEmpty()) {
            throw new ProductNotFoundException(
                    String.format("Product with id '%s' not found for user '%s'", productId, user.getId())
            );
        }

        var userProduct = userProductOptional.get();

        userProduct.setNotify(Boolean.FALSE);

        return userProductRepository.save(userProduct).isNotify();
    }


}
