package ru.tbank.pp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.pp.client.YandexClient;
import ru.tbank.pp.dto.yandex.Message;
import ru.tbank.pp.dto.yandex.request.CompletionOptions;
import ru.tbank.pp.dto.yandex.request.YandexGptRequest;
import ru.tbank.pp.exception.YandexGptResponseNotFoundException;
import ru.tbank.pp.model.ProductsGptResponse;
import ru.tbank.pp.model.ProductsIdList;
import ru.tbank.pp.properties.YandexGptProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptHelperService {
    private final ProductService productService;

    private final YandexGptProperties yandexGptProperties;
    private final YandexClient yandexClient;

    private final ObjectMapper objectMapper;

    private static final String YANDEX_USER_ROLE = "user";
    private static final String YANDEX_SYSTEM_ROLE = "system";

    public ProductsGptResponse getGptResponse(ProductsIdList idList) {
        var products = productService.getProductDetailList(idList.getIds());

        var objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        var stringJsonProducts = "";
        try {
            stringJsonProducts = objectWriter.writeValueAsString(products);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        YandexGptRequest request = new YandexGptRequest();
        try {
            request = getYandexGptRequest(stringJsonProducts);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        var gptResponse = yandexClient.getGptHelp(request);

        if (gptResponse == null) {
            throw new YandexGptResponseNotFoundException("Произошла ошибка при получении ответа от YandexGpt");
        }
        var response = new ProductsGptResponse();
        response.setGptOpinion(gptResponse.getResult()
                .getAlternatives()
                .getFirst()
                .getMessage()
                .getText());

        return response;
    }

    private YandexGptRequest getYandexGptRequest(String products) throws IOException {
        var completionOptions = new CompletionOptions(false, 0.6, 10000);

        var command = Files.readString(Path.of(yandexGptProperties.getCommandPathForIde()));

        var systemMessage = new Message(YANDEX_SYSTEM_ROLE, command);
        var userMessage = new Message(YANDEX_USER_ROLE, products);

        return new YandexGptRequest(
                "gpt://" + yandexGptProperties.getFolderId() + yandexGptProperties.getModel(),
                completionOptions,
                List.of(systemMessage, userMessage));
    }
}
