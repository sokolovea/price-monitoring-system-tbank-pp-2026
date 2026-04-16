package ru.tbank.pp.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.exception.ProductNotFoundException;
import ru.tbank.pp.model.ProductsMarketplace;
import ru.tbank.pp.model.ProductsPriceHistory;
import ru.tbank.pp.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPriceMapperTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductPriceMapper productPriceMapper;

    @Test
    void mapToProductsPriceHistory_Success() {
        ProductPrice productPrice = createTestProductPrice();

        ProductsPriceHistory result = productPriceMapper.mapToProductsPriceHistory(productPrice);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getDate()).isNotNull();
    }

    @Test
    void toProductPrice_Success() {
        Product product = createTestProduct();

        UpdatePriceResponse dto = new UpdatePriceResponse();
        dto.setId(1L);
        dto.setPrice(new BigDecimal("1000"));
        dto.setDate(Instant.EPOCH);

        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        ProductPrice result = productPriceMapper.toProductPrice(dto);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getId().getProductId()).isEqualTo(1L);
        assertThat(result.getProduct()).isEqualTo(product);
    }

    @Test
    void toProductPrice_ProductNotFound_ThrowsException() {
        UpdatePriceResponse dto = new UpdatePriceResponse();
        dto.setId(99L);
        dto.setPrice(new BigDecimal("1000"));
        dto.setDate(Instant.EPOCH);

        when(productRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> productPriceMapper.toProductPrice(dto))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with id '99' not found");
    }

    private ProductPrice createTestProductPrice() {
        ProductPriceId productPriceId = new ProductPriceId();
        productPriceId.setProductId(1L);
        productPriceId.setCheckDate(Instant.EPOCH);

        ProductPrice productPrice = new ProductPrice();
        productPrice.setId(productPriceId);
        productPrice.setPrice(new BigDecimal("1000"));
        return productPrice;
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setBrand("Test Brand");
        product.setUrl("https://example.com/product");
        product.setArticle("12345");
        product.setDescription("Test Description");
        product.setOptionName("Test Option");
        product.setOptionId("100");
        product.setImage("https://example.com/image.jpg");
        product.setMarketplace(ProductsMarketplace.OZON);
        return product;
    }
}
