package ru.tbank.pp.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.ProductInfo;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.model.ProductsMarketplace;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.model.ProductsProductPreview;
import ru.tbank.pp.service.ProductPriceService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private ProductPriceService productPriceService;

    @InjectMocks
    private ProductMapper productMapper;

    @Test
    void toProductsProduct_Success() {
        Product product = createTestProduct();

        when(productPriceService.getCurrentPrice(1L)).thenReturn(new BigDecimal("1000"));

        ProductsProduct result = productMapper.toProductsProduct(product);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getBrand()).isEqualTo("Test Brand");
        assertThat(result.getUrl()).isEqualTo("https://example.com/product");
        assertThat(result.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getImage()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.getMarketplace()).isEqualTo(ProductsMarketplace.OZON);
    }

    @Test
    void toProductsProductDetail_Success() {
        Product product = createTestProduct();

        when(productPriceService.getCurrentPrice(1L)).thenReturn(new BigDecimal("1000"));

        var result = productMapper.toProductsProductDetail(product);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    void toProductsProductPreview_Success() {
        Product product = createTestProduct();

        when(productPriceService.getCurrentPrice(1L)).thenReturn(new BigDecimal("1000"));

        ProductsProductPreview result = productMapper.toProductsProductPreview(product);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Product");
        assertThat(result.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getImage()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    void toProduct_Success() {
        ProductInfo dto = ProductInfo.builder().build();
        dto.setName("New Product");
        dto.setBrand("New Brand");
        dto.setSku("12345");
        dto.setUrl("https://example.com/new");
        dto.setMarketplace(ProductsMarketplace.WILDBERRIES);
        dto.setOptionId("100");
        dto.setOptionName("Test Option");

        Product result = productMapper.toProduct(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getBrand()).isEqualTo("New Brand");
        assertThat(result.getArticle()).isEqualTo("12345");
        assertThat(result.getUrl()).isEqualTo("https://example.com/new");
        assertThat(result.getOptionId()).isEqualTo("100");
        assertThat(result.getOptionName()).isEqualTo("Test Option");
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");
        product.setUrl("https://example.com/product");
        product.setArticle("12345");
        product.setIsTracked(true);
        product.setOptionName("Test Option");
        product.setOptionId("100");
        product.setImage("https://example.com/image.jpg");
        product.setMarketplace(ProductsMarketplace.OZON);
        return product;
    }
}
