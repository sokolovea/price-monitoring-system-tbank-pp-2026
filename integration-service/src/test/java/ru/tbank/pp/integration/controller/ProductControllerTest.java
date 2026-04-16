package ru.tbank.pp.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.dto.NormalizedReference;
import ru.tbank.dto.SimilarProducts;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderFactory;
import ru.tbank.pp.integration.provider.UrlParser;
import ru.tbank.pp.model.ProductsMarketplace;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ProductController")
class ProductControllerTest {

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private UrlParser urlParser;

    @Mock
    private ProductProvider productProvider;

    @InjectMocks
    private ProductController productController;

    private ProductInfo productInfo;

    @BeforeEach
    void setUp() {
        productInfo = ProductInfo.builder()
                .name("Test Product")
                .sku("12345")
                .price(1000L)
                .marketplace(ProductsMarketplace.WILDBERRIES)
                .build();
    }

    @Test
    @DisplayName("getProduct должен возвращать информацию о продукте")
    void getProduct_shouldReturnProductInfo() {
        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.getProductInfo(any(ProductReference.class))).thenReturn(productInfo);

        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.wildberries.ru/catalog/12345/detail.aspx");
        ref.setMarketplace(ProductsMarketplace.WILDBERRIES);
        ref.setSku("12345");

        ResponseEntity<ProductInfo> response = productController.getProduct(ref);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productInfo, response.getBody());
        verify(providerFactory).getProvider(ProductsMarketplace.WILDBERRIES);
        verify(productProvider).getProductInfo(any(ProductReference.class));
    }

    @Test
    @DisplayName("getProduct должен пробрасывать исключения от провайдера")
    void getProduct_shouldPropagateProviderException() {
        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.getProductInfo(any(ProductReference.class)))
                .thenThrow(new RuntimeException("Provider error"));

        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.wildberries.ru/catalog/12345/detail.aspx");
        ref.setMarketplace(ProductsMarketplace.WILDBERRIES);

        assertThrows(RuntimeException.class, () -> productController.getProduct(ref));
    }

    @Test
    @DisplayName("getSimilarProduct должен возвращать SimilarProducts")
    void getSimilarProduct_shouldReturnSimilarProducts() {
        NormalizedReference normalizedRef = new NormalizedReference("12345", ProductsMarketplace.WILDBERRIES, null);
        SimilarProducts similarProducts = new SimilarProducts(List.of(productInfo));

        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.normalize(any(ProductReference.class))).thenReturn(normalizedRef);
        when(productProvider.getSimilarProducts(normalizedRef)).thenReturn(similarProducts);

        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.wildberries.ru/catalog/12345/detail.aspx");
        ref.setMarketplace(ProductsMarketplace.WILDBERRIES);

        ResponseEntity<SimilarProducts> response = productController.getSimilarProduct(ref);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(similarProducts, response.getBody());
    }

    @Test
    @DisplayName("getSimilarProduct должен возвращать пустой список если похожих продуктов нет")
    void getSimilarProduct_shouldReturnEmptyList() {
        NormalizedReference normalizedRef = new NormalizedReference("12345", ProductsMarketplace.WILDBERRIES, null);
        SimilarProducts similarProducts = new SimilarProducts(List.of());

        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.normalize(any(ProductReference.class))).thenReturn(normalizedRef);
        when(productProvider.getSimilarProducts(normalizedRef)).thenReturn(similarProducts);

        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.wildberries.ru/catalog/12345/detail.aspx");
        ref.setMarketplace(ProductsMarketplace.WILDBERRIES);

        ResponseEntity<SimilarProducts> response = productController.getSimilarProduct(ref);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getProducts().isEmpty());
    }
}