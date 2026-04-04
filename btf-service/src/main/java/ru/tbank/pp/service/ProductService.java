package ru.tbank.pp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.entity.UserProductId;
import ru.tbank.pp.exception.AuthException;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.exception.UserNotFoundException;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.mapper.UserProductMapper;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.model.ProductsProductDetail;
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


}
