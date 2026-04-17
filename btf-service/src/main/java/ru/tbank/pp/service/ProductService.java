package ru.tbank.pp.service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.client.IntegrationClient;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.entity.UserProductId;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.mapper.UserProductMapper;
import ru.tbank.pp.model.*;
import ru.tbank.pp.repository.ProductPriceRepository;
import ru.tbank.pp.repository.ProductRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserProductRepository userProductRepository;
    private final ProductPriceRepository productPriceRepository;

    private final ProductPriceService productPriceService;
    private final UserService userService;

    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;
    private final UserProductMapper userProductMapper;

    private final IntegrationClient integrationClient;

    public List<ProductsProduct> getAllUserProducts() {
        var user = userService.getUserFromCridentials();
        var userId = user.getId();

        var userProducts = userProductRepository.findByUserId(userId);

        return userProducts.stream()
                .map(up -> productMapper.toProductsProduct(up.getProduct()))
                .toList();
    }

    public ProductsProductDetail getProductDetail(Long productId) {
        var user = userService.getUserFromCridentials();

        var productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException(String.format("Product with id '%s' not found", productId));
        }

        var product = productOptional.get();
        var productDetail = productMapper.toProductsProductDetail(product);

        return setDetailParameters(productDetail, user.getId());
    }

    private ProductsProductDetail setDetailParameters(ProductsProductDetail productsProductDetail, Long userId) {
        var productId = productsProductDetail.getId();
        var productPrices = productPriceService.getProductPrices(productId);
        var priceHistory = productPrices.stream()
                .map(productPriceMapper::mapToProductsPriceHistory)
                .toList();

        var userProduct = getUserProduct(productId, userId);

        var productsNotification = userProductMapper.toProductsNotification(userProduct);

        if (priceHistory.size() >= 2) {
            var lastPrice = priceHistory.getLast().getPrice();
            var previousPrice = priceHistory.get(priceHistory.size() - 2).getPrice();
            var priceChange = lastPrice.subtract(previousPrice);
            var priceChangePercent = priceChange
                    .divide(lastPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);

            productsProductDetail.setPriceChange(priceHistory.getLast().getPrice());
            productsProductDetail.setPriceChangePercent(priceChangePercent.floatValue());
        }

        productsProductDetail.setPriceHistory(priceHistory);
        productsProductDetail.setNotification(productsNotification);
        return productsProductDetail;
    }

    @Transactional
    public ProductsProduct addProduct(ProductsUrl productUrl) {
        var user = userService.getUserFromCridentials();

        var product = getProductByUrl(productUrl.getUrl());

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

    public ProductsProductPreview getProductPreview(ProductsUrl productUrl) {
        var product = getProductByUrl(productUrl.getUrl());

        return productMapper.toProductsProductPreview(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        var user = userService.getUserFromCridentials();

        var userProductId = new UserProductId();
        userProductId.setUserId(user.getId());
        userProductId.setProductId(productId);

        userProductRepository.deleteById(userProductId);
    }

    @Transactional
    public ProductsNotification subscribeNotification(Long productId, ProductsNotificationUpdate productsNotificationUpdate) {
        var user = userService.getUserFromCridentials();

        var userProduct = getUserProduct(productId, user.getId());

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
        var user = userService.getUserFromCridentials();
        var userProduct = getUserProduct(productId, user.getId());

        userProduct.setNotify(Boolean.FALSE);

        return userProductRepository.save(userProduct).getNotify();
    }

    @Transactional
    public void createNewProduct(ProductInfo createProductDto) {
        var product = productMapper.toProduct(createProductDto);
        product = productRepository.save(product);

        var productPriceRequest = new UpdatePriceResponse();
        productPriceRequest.setId(product.getId());
        productPriceRequest.setPrice(BigDecimal.valueOf(createProductDto.getPrice(), 2));
        productPriceRequest.setDate(Instant.now());

        var productPrice = productPriceMapper.toProductPrice(productPriceRequest);
        productPriceRepository.save(productPrice);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(
                        String.format("Product with id '%s' not found", productId)
                )
        );
    }

    public List<ProductsProductDetail> getProductDetailList(List<Long> ids) {
        var user = userService.getUserFromCridentials();

        var productsOptional = productRepository.findAllById(ids);
        if (productsOptional.isEmpty()) {
            throw new ProductNotFoundException("Products not found");
        }

        return productsOptional.stream()
                .map(productMapper::toProductsProductDetail)
                .map(pd ->setDetailParameters(pd, user.getId()))
                .toList();
    }

    private UserProduct getUserProduct(Long productId, Long userId) {
        var userProductId = new UserProductId();
        userProductId.setUserId(userId);
        userProductId.setProductId(productId);

        var userProductOptional = userProductRepository.findById(userProductId);
        if (userProductOptional.isEmpty()) {
            throw new ProductNotFoundException(
                    String.format("Product with id '%s' not found for user '%s'", productId, userId)
            );
        }
        return userProductOptional.get();
    }

    private Product getProductByUrl(String url) {
        var productOptional = productRepository.findByUrl(url);

        Product result;
        if (productOptional.isEmpty()) {
            ProductReference productReference = new ProductReference();
            productReference.setUrl(url);

            Optional<ProductInfo> requestResult = integrationClient.sendProductRequest(productReference);
            Instant responseTimestamp = Instant.now();
            if (requestResult.isEmpty()) {
                log.debug("Request for product failed! Product url: {}", url);
                //todo что-то с этим сдлеать
                throw new RuntimeException("Couldn't get product.");
            }

            result = productRepository.save(productMapper.toProduct(requestResult.get()));
            
            //todo вынести в маппер (?)
            UpdatePriceResponse firstPrice = new UpdatePriceResponse();
            firstPrice.setDate(responseTimestamp);
            firstPrice.setId(result.getId());
            firstPrice.setPrice(BigDecimal.valueOf(requestResult.get().getPrice(), 2));
            productPriceRepository.save(productPriceMapper.toProductPrice(firstPrice));
        } else {
            result = productOptional.get();
        }

        return result;
    }

    public ProductsProductRecommendations getProductRecommendations(Long productId, Integer limit, Integer offset) {
        var product = getProduct(productId);
        var productReference = new ProductReference();
        productReference.setUrl(product.getUrl());
        //var productReference = productMapper.toProductReference(product);
        var recommendationsOptional = integrationClient.sendSimilarRequest(productReference);

        if (recommendationsOptional.isEmpty()) {
            throw new ProductNotFoundException("Product Recommendations not found");
        }
        var recommendations = recommendationsOptional.get();

        var products = recommendations.getProducts().stream().map(productMapper::toProductsProduct).toList();

        var recommendationsRecommendations = new ProductsProductRecommendations();
        recommendationsRecommendations.setTotal(products.size());
        recommendationsRecommendations.setLimit(limit);
        recommendationsRecommendations.setOffset(offset);
        recommendationsRecommendations.setItems(products);
        return recommendationsRecommendations;

    }

}
