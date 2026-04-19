package ru.tbank.pp.integration.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderFactory;
import ru.tbank.pp.model.ProductsMarketplace;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ProductHandler")
class ProductHandlerTest {

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private ProductProvider productProvider;

    @InjectMocks
    private ProductHandler productHandler;

    private UpdatePriceRequest priceRequest;
    private UpdatePriceResponse priceResponse;

    @BeforeEach
    void setUp() {
        priceRequest = new UpdatePriceRequest();
        priceRequest.setId(1L);
        priceRequest.setSku("12345");
        priceRequest.setMarketplace(ProductsMarketplace.WILDBERRIES);
        priceRequest.setOptionId("1");

        priceResponse = new UpdatePriceResponse();
        priceResponse.setId(1L);
        priceResponse.setPrice(BigDecimal.valueOf(1000, 2));
        priceResponse.setDate(Instant.now());
    }

    @Test
    @DisplayName("getPrices должен возвращать цены для списка запросов")
    void getPrices_shouldReturnPricesForRequests() {
        List<UpdatePriceRequest> requests = List.of(priceRequest);
        List<UpdatePriceResponse> expectedResponses = List.of(priceResponse);

        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.getPriceInfo(anyList())).thenReturn(expectedResponses);

        List<UpdatePriceResponse> result = productHandler.getPrices(requests);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(priceResponse.getPrice(), result.get(0).getPrice());
        verify(providerFactory).getProvider(ProductsMarketplace.WILDBERRIES);
        verify(productProvider).getPriceInfo(requests);
    }

//    @Test
//    @DisplayName("getPrices должен обрабатывать запросы для разных маркетплейсов")
//    void getPrices_shouldHandleMultipleMarketplaces() {
//        UpdatePriceRequest ozonRequest = new UpdatePriceRequest();
//        ozonRequest.setId(2L);
//        ozonRequest.setSku("67890");
//        ozonRequest.setMarketplace(ProductsMarketplace.OZON);
//
//        List<UpdatePriceRequest> requests = List.of(priceRequest, ozonRequest);
//        List<UpdatePriceResponse> wbResponses = List.of(priceResponse);
//        List<UpdatePriceResponse> ozonResponses = List.of();
//
//        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
//        when(providerFactory.getProvider(ProductsMarketplace.OZON)).thenThrow(
//                new ru.tbank.pp.integration.provider.exception.UnsupportedProviderException("Unsupported")
//        );
//        when(productProvider.getPriceInfo(anyList())).thenReturn(wbResponses);
//
//        assertThrows(ru.tbank.pp.integration.provider.exception.UnsupportedProviderException.class,
//                () -> productHandler.getPrices(requests));
//    }

    @Test
    @DisplayName("getPrices должен возвращать пустой список для пустого ввода")
    void getPrices_shouldReturnEmptyListForEmptyInput() {
        List<UpdatePriceRequest> emptyRequests = List.of();

        List<UpdatePriceResponse> result = productHandler.getPrices(emptyRequests);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(providerFactory, productProvider);
    }

    @Test
    @DisplayName("getPrices должен пробрасывать исключения от провайдера")
    void getPrices_shouldPropagateProviderException() {
        List<UpdatePriceRequest> requests = List.of(priceRequest);

        when(providerFactory.getProvider(ProductsMarketplace.WILDBERRIES)).thenReturn(productProvider);
        when(productProvider.getPriceInfo(anyList()))
                .thenThrow(new RuntimeException("Provider communication error"));

        assertThrows(RuntimeException.class, () -> productHandler.getPrices(requests));
    }
}
