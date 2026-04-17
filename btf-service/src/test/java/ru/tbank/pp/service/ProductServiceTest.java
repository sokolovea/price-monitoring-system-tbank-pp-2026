package ru.tbank.pp.service;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.entity.UserProductId;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.mapper.ProductMapper;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.mapper.UserProductMapper;
import ru.tbank.pp.model.*;
import ru.tbank.pp.repository.ProductPriceRepository;
import ru.tbank.pp.repository.ProductRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserProductRepository userProductRepository;

    @Mock
    private ProductPriceRepository productPriceRepository;

    @Mock
    private ProductPriceService productPriceService;

    @Mock
    private UserService userService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductPriceMapper productPriceMapper;

    @Mock
    private UserProductMapper userProductMapper;

    @InjectMocks
    private ProductService productService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole("USER");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setBrand("Test Brand");
        testProduct.setUrl("https://example.com/product");
        testProduct.setArticle("12345");
        testProduct.setIsTracked(true);
        testProduct.setOptionName("Test Option");
        testProduct.setOptionId("100");
        testProduct.setImage("https://example.com/image.jpg");
        testProduct.setMarketplace(ProductsMarketplace.OZON);
    }

    @Test
    void getAllUserProducts_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);

        UserProduct userProduct = new UserProduct();
        userProduct.setProduct(testProduct);
        userProduct.setUser(testUser);

        when(userProductRepository.findByUserId(1L)).thenReturn(List.of(userProduct));

        ProductsProduct productsProduct = new ProductsProduct();
        productsProduct.setId(1L);
        productsProduct.setName("Test Product");
        when(productMapper.toProductsProduct(testProduct)).thenReturn(productsProduct);

        List<ProductsProduct> result = productService.getAllUserProducts();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Product");
        verify(userService).getUserFromCridentials();
        verify(userProductRepository).findByUserId(1L);
    }

    @Test
    void getProductDetail_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductPrice productPrice = new ProductPrice();
        ProductPriceId productPriceId = new ProductPriceId();
        productPriceId.setProductId(1L);
        productPriceId.setCheckDate(Instant.now());
        productPrice.setId(productPriceId);
        productPrice.setPrice(new BigDecimal("1000"));
        productPrice.setProduct(testProduct);

        when(productPriceService.getProductPrices(1L)).thenReturn(List.of(productPrice));

        ProductsPriceHistory priceHistory = new ProductsPriceHistory();
        priceHistory.setPrice(new BigDecimal("1000"));
        when(productPriceMapper.mapToProductsPriceHistory(productPrice)).thenReturn(priceHistory);

        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(true);
        userProduct.setThresholdPrice(new BigDecimal("900"));
        when(userProductRepository.findById(any(UserProductId.class))).thenReturn(Optional.of(userProduct));

        ProductsNotification productsNotification = new ProductsNotification();
        productsNotification.setEnabled(true);
        productsNotification.setThresholdPrice(new BigDecimal("900"));
        when(userProductMapper.toProductsNotification(userProduct)).thenReturn(productsNotification);

        ProductsProductDetail productDetail = new ProductsProductDetail();
        productDetail.setId(1L);
        productDetail.setName("Test Product");
        when(productMapper.toProductsProductDetail(testProduct)).thenReturn(productDetail);

        ProductsProductDetail result = productService.getProductDetail(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPriceHistory()).hasSize(1);
        assertThat(result.getNotification()).isNotNull();
    }

    @Test
    void getProductDetail_ProductNotFound_ThrowsException() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductDetail(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id '99' not found");
    }

//    @Test
//    void getProductDetail_UserProductNotFound_ThrowsException() {
//        when(userService.getUserFromCridentials()).thenReturn(testUser);
//        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
//        when(productPriceService.getProductPrices(1L)).thenReturn(List.of());
//        when(userProductRepository.findById(any(UserProductId.class))).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> productService.getProductDetail(1L))
//                .isInstanceOf(ProductNotFoundException.class)
//                .hasMessageContaining("Product with id '1' not found for user '1'");
//    }

    @Test
    void addProduct_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);
        when(productRepository.findByUrl("https://example.com/product")).thenReturn(Optional.of(testProduct));

        ProductsProduct productsProduct = new ProductsProduct();
        productsProduct.setId(1L);
        productsProduct.setName("Test Product");
        when(productMapper.toProductsProduct(testProduct)).thenReturn(productsProduct);
        when(userProductRepository.save(any(UserProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductsUrl productUrl = new ProductsUrl();
        productUrl.setUrl("https://example.com/product");

        ProductsProduct result = productService.addProduct(productUrl);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(userProductRepository).save(any(UserProduct.class));
    }

    @Test
    void getProductPreview_Success() {
        when(productRepository.findByUrl("https://example.com/product")).thenReturn(Optional.of(testProduct));

        ProductsProductPreview preview = new ProductsProductPreview();
        preview.setTitle("Test Product");
        preview.setCurrentPrice(new BigDecimal("1000"));
        when(productMapper.toProductsProductPreview(testProduct)).thenReturn(preview);

        ProductsUrl productUrl = new ProductsUrl();
        productUrl.setUrl("https://example.com/product");

        ProductsProductPreview result = productService.getProductPreview(productUrl);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Product");
    }

    @Test
    void deleteProduct_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);
        doNothing().when(userProductRepository).deleteById(any(UserProductId.class));

        productService.deleteProduct(1L);

        verify(userProductRepository).deleteById(any(UserProductId.class));
    }

    @Test
    void subscribeNotification_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);

        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(false);
        when(userProductRepository.findById(any(UserProductId.class))).thenReturn(Optional.of(userProduct));
        when(userProductRepository.save(any(UserProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductsNotificationUpdate notificationUpdate = new ProductsNotificationUpdate();
        notificationUpdate.setEnabled(true);
        notificationUpdate.setThresholdPrice(new BigDecimal("800"));

        ProductsNotification result = productService.subscribeNotification(1L, notificationUpdate);

        assertThat(result).isNotNull();
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getThresholdPrice()).isEqualByComparingTo(new BigDecimal("800"));
        assertThat(userProduct.getNotify()).isTrue();
    }

    @Test
    void unsubscribeNotification_Success() {
        when(userService.getUserFromCridentials()).thenReturn(testUser);

        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(true);
        when(userProductRepository.findById(any(UserProductId.class))).thenReturn(Optional.of(userProduct));
        when(userProductRepository.save(any(UserProduct.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boolean result = productService.unsubscribeNotification(1L);

        assertThat(result).isFalse();
        assertThat(userProduct.getNotify()).isFalse();
    }

    @Test
    void createNewProduct_Success() {
        ProductInfo createProductDto = ProductInfo.builder().build();
        createProductDto.setName("New Product");
        createProductDto.setBrand("New Brand");
        createProductDto.setSku("999");
        createProductDto.setUrl("https://example.com/new");
        createProductDto.setMarketplace(ProductsMarketplace.WILDBERRIES);
        createProductDto.setOptionId("200");
        createProductDto.setOptionName("New Option");
        createProductDto.setPrice(1500L);

        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setName("New Product");

        when(productMapper.toProduct(createProductDto)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(newProduct);
        when(productPriceMapper.toProductPrice(any(UpdatePriceResponse.class)))
                .thenReturn(new ProductPrice());
        when(productPriceRepository.save(any(ProductPrice.class))).thenReturn(new ProductPrice());

        productService.createNewProduct(createProductDto);

        verify(productRepository).save(newProduct);
        verify(productPriceRepository).save(any(ProductPrice.class));
    }

    @Test
    void getProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProduct(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    void getProduct_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id '99' not found");
    }

//    @Test
//    void getProductDetailList_Success() {
//        when(productRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(testProduct));
//
//        ProductsProductDetail productDetail = new ProductsProductDetail();
//        productDetail.setId(1L);
//        productDetail.setName("Test Product");
//        when(productMapper.toProductsProductDetail(testProduct)).thenReturn(productDetail);
//
//        List<ProductsProductDetail> result = productService.getProductDetailList(List.of(1L, 2L));
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getName()).isEqualTo("Test Product");
//    }

    @Test
    void getProductDetailList_NotFound_ThrowsException() {
        when(productRepository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThatThrownBy(() -> productService.getProductDetailList(List.of(99L)))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Products not found");
    }
}
