package ru.tbank.pp.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.CreateProductDto;
import ru.tbank.enums.Marketplace;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.model.ProductsMarketplace;
import ru.tbank.pp.model.ProductsPriceHistory;
import ru.tbank.pp.model.ProductsProduct;
import ru.tbank.pp.model.ProductsProductPreview;
import ru.tbank.pp.repository.ProductRepository;
import ru.tbank.pp.service.ProductPriceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
        CreateProductDto dto = new CreateProductDto();
        dto.setName("New Product");
        dto.setBrand("New Brand");
        dto.setSku(12345L);
        dto.setUrl("https://example.com/new");
        dto.setMarketplace(Marketplace.Wildberries);
        dto.setOptionId(100L);
        dto.setOptionName("Test Option");

        Product result = productMapper.toProduct(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Product");
        assertThat(result.getBrand()).isEqualTo("New Brand");
        assertThat(result.getArticle()).isEqualTo(12345L);
        assertThat(result.getUrl()).isEqualTo("https://example.com/new");
        assertThat(result.getOptionId()).isEqualTo(100L);
        assertThat(result.getOptionName()).isEqualTo("Test Option");
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");
        product.setUrl("https://example.com/product");
        product.setArticle(12345L);
        product.setDescription("Test Description");
        product.setTracked(true);
        product.setOptionName("Test Option");
        product.setOptionId(100L);
        product.setImage("https://example.com/image.jpg");
        product.setMarketplace(ProductsMarketplace.OZON);
        return product;
    }
}
