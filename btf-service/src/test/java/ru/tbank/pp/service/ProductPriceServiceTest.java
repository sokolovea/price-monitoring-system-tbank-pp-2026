package ru.tbank.pp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.entity.Product;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.ProductPriceId;
import ru.tbank.pp.entity.UserNotification;
import ru.tbank.pp.entity.UserNotificationId;
import ru.tbank.pp.model.ServiceConnectionService;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.repository.ProductPriceRepository;
import ru.tbank.pp.repository.UserNotificationRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPriceServiceTest {

    @Mock
    private ProductPriceRepository productPriceRepository;

    @Mock
    private UserProductRepository userProductRepository;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private ProductPriceMapper productPriceMapper;

    @Mock
    private ru.tbank.pp.client.NotificationClient notificationClient;

    @InjectMocks
    private ProductPriceService productPriceService;

    private Product testProduct;
    private ProductPrice testProductPrice;
    private UpdatePriceResponse priceResponseDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setUrl("https://example.com/product");
        testProduct.setImage("https://example.com/image.jpg");

        testProductPrice = new ProductPrice();
        ProductPriceId productPriceId = new ProductPriceId();
        productPriceId.setProductId(1L);
        productPriceId.setCheckDate(Instant.EPOCH);
        testProductPrice.setId(productPriceId);
        testProductPrice.setProduct(testProduct);
        testProductPrice.setPrice(new BigDecimal("1000.00"));

        priceResponseDto = new UpdatePriceResponse();
        priceResponseDto.setId(1L);
        priceResponseDto.setPrice(new BigDecimal("900.00"));
        priceResponseDto.setDate(Instant.EPOCH);
    }

    @Test
    void getCurrentPrice_Success() {
        when(productPriceRepository.findByProductIdOrderByIdCheckDateDesc(1L))
                .thenReturn(List.of(testProductPrice));

        BigDecimal result = productPriceService.getCurrentPrice(1L);

        assertThat(result).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(productPriceRepository).findByProductIdOrderByIdCheckDateDesc(1L);
    }

    @Test
    void getProductPrices_Success() {
        when(productPriceRepository.findByProductIdOrderByIdCheckDateDesc(1L))
                .thenReturn(List.of(testProductPrice));

        List<ProductPrice> result = productPriceService.getProductPrices(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void setProductPrice_NoUsersToNotify() {
        ProductPrice mappedProductPrice = new ProductPrice();
        mappedProductPrice.setProduct(testProduct);

        when(productPriceMapper.toProductPrice(priceResponseDto)).thenReturn(mappedProductPrice);
        when(productPriceRepository.save(mappedProductPrice)).thenReturn(mappedProductPrice);
        when(userProductRepository.findUserIdsForNotification(1L, new BigDecimal("900.00")))
                .thenReturn(List.of());

        productPriceService.setProductPrice(priceResponseDto);

        verify(productPriceRepository).save(mappedProductPrice);
        verify(notificationClient, never()).sendNotification(any());
    }

    @Test
    void setProductPrice_WithUsersToNotify() {
        ProductPrice mappedProductPrice = new ProductPrice();
        mappedProductPrice.setProduct(testProduct);

        UserNotificationId userNotificationId = new UserNotificationId();
        userNotificationId.setUserId(10L);
        userNotificationId.setNotificationService(ru.tbank.pp.model.ServiceConnectionService.TELEGRAM);

        ru.tbank.pp.entity.UserNotification userNotification = new ru.tbank.pp.entity.UserNotification();
        userNotification.setId(userNotificationId);
        userNotification.setInternalId(12345L);

        when(productPriceMapper.toProductPrice(priceResponseDto)).thenReturn(mappedProductPrice);
        when(productPriceRepository.save(mappedProductPrice)).thenReturn(mappedProductPrice);
        when(userProductRepository.findUserIdsForNotification(1L, new BigDecimal("900.00")))
                .thenReturn(List.of(10L));
        when(userNotificationRepository.findById(any(UserNotificationId.class)))
                .thenReturn(Optional.of(userNotification));

        productPriceService.setProductPrice(priceResponseDto);

        verify(notificationClient).sendNotification(any());
    }
}
