package ru.tbank.pp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.tbank.pp.config.RestClientConfig;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;

@Component
@RequiredArgsConstructor
public class BackendClient {
    private final RestClient restClient;

    public boolean checkIfUserExists(ServiceConnectionStatusCheckRequest connectionStatusCheckRequest) {
        var result =  restClient
                .post()
                .uri("/service/check-status")
                .body(connectionStatusCheckRequest)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    public void connectUserService(ServiceConnectionConnectRequest serviceConnectionConnectRequest) {
        restClient
                .post()
                .uri("/service/connect")
                .body(serviceConnectionConnectRequest)
                .retrieve()
                .body(void.class);
    }

}
