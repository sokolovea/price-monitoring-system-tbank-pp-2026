package ru.tbank.pp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tbank.pp.client.YandexClient;
import ru.tbank.pp.dto.yandex.Message;
import ru.tbank.pp.dto.yandex.request.YandexGptRequest;
import ru.tbank.pp.dto.yandex.response.Alternative;
import ru.tbank.pp.dto.yandex.response.Result;
import ru.tbank.pp.dto.yandex.response.Usage;
import ru.tbank.pp.dto.yandex.response.YandexGptResponse;
import ru.tbank.pp.exception.YandexGptResponseNotFoundException;
import ru.tbank.pp.model.ProductsIdList;
import ru.tbank.pp.model.ProductsProductDetail;
import ru.tbank.pp.properties.YandexGptProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GptHelperServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private YandexGptProperties yandexGptProperties;

    @Mock
    private YandexClient yandexClient;

    private ObjectMapper objectMapper;

    @InjectMocks
    private GptHelperService gptHelperService;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Создаём временный файл с командой для GPT
        Path tempCommandFile = Files.createTempFile("gpt-command", ".txt");
        Files.writeString(tempCommandFile, "Ты полезный ассистент");
        tempCommandFile.toFile().deleteOnExit();

        ReflectionTestUtils.setField(gptHelperService, "objectMapper", objectMapper);

        when(yandexGptProperties.getCommandPath()).thenReturn(tempCommandFile.toString());
        when(yandexGptProperties.getFolderId()).thenReturn("test-folder-id");
        when(yandexGptProperties.getModel()).thenReturn("/v1");
    }

    @Test
    void getGptResponse_Success() {
        ProductsIdList idList = new ProductsIdList();
        idList.setIds(List.of(1L, 2L));

        ProductsProductDetail product1 = new ProductsProductDetail();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setBrand("Brand 1");
        product1.setUrl("https://example.com/1");
        product1.setCurrentPrice(new BigDecimal("1000"));
        product1.setLastChecked(OffsetDateTime.now());

        ProductsProductDetail product2 = new ProductsProductDetail();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setBrand("Brand 2");
        product2.setUrl("https://example.com/2");
        product2.setCurrentPrice(new BigDecimal("2000"));
        product2.setLastChecked(OffsetDateTime.now());

        when(productService.getProductDetailList(List.of(1L, 2L)))
                .thenReturn(List.of(product1, product2));

        Alternative alternative = new Alternative(new Message("assistant", "Это хороший товар!"), "OK");

        Result result = new Result(List.of(alternative), new Usage("100", "50", "150", null), "v1");

        YandexGptResponse gptResponse = new YandexGptResponse(result);

        when(yandexClient.getGptHelp(any(YandexGptRequest.class))).thenReturn(gptResponse);

        var response = gptHelperService.getGptResponse(idList);

        assertThat(response).isNotNull();
        assertThat(response.getGptOpinion()).isEqualTo("Это хороший товар!");
        verify(productService).getProductDetailList(List.of(1L, 2L));
        verify(yandexClient).getGptHelp(any(YandexGptRequest.class));
    }

    @Test
    void getGptResponse_NullGptResponse_ThrowsException() {
        ProductsIdList idList = new ProductsIdList();
        idList.setIds(List.of(1L));

        when(productService.getProductDetailList(List.of(1L)))
                .thenReturn(List.of());
        when(yandexClient.getGptHelp(any(YandexGptRequest.class))).thenReturn(null);

        assertThatThrownBy(() -> gptHelperService.getGptResponse(idList))
                .isInstanceOf(YandexGptResponseNotFoundException.class)
                .hasMessageContaining("Произошла ошибка при получении ответа от YandexGpt");
    }
}
