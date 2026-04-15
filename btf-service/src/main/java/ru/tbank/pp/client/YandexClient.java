package ru.tbank.pp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.dto.yandex.request.YandexGptRequest;
import ru.tbank.pp.dto.yandex.response.YandexGptResponse;
import ru.tbank.pp.properties.YandexGptProperties;

@Component
public class YandexClient {
    private final RestClient restClient;
    private final YandexGptProperties yandexGptProperties;

    public YandexClient(@Qualifier("yandex") RestClient restClient, YandexGptProperties yandexGptProperties) {
        this.restClient = restClient;
        this.yandexGptProperties = yandexGptProperties;
    }


    public YandexGptResponse getGptHelp(YandexGptRequest request) {
        return restClient
                .post()
                .header(HttpHeaders.AUTHORIZATION, "Api-Key " + yandexGptProperties.getApiKey())
                .header("x-folder-id", yandexGptProperties.getFolderId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(YandexGptResponse.class);
    }
}
