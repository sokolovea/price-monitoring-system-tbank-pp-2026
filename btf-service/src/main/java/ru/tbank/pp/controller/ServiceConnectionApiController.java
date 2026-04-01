package ru.tbank.pp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import ru.tbank.pp.api.ServiceApi;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;

import java.util.Optional;

@RestController
public class ServiceConnectionApiController implements ServiceApi {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ServiceApi.super.getRequest();
    }

    @Override
    public ResponseEntity<Boolean> serviceConnectionCheckStatus(ServiceConnectionStatusCheckRequest serviceConnectionStatusCheckRequest) {
        return ServiceApi.super.serviceConnectionCheckStatus(serviceConnectionStatusCheckRequest);
    }

    @Override
    public ResponseEntity<Void> serviceConnectionConnect(ServiceConnectionConnectRequest serviceConnectionConnectRequest) {
        return ServiceApi.super.serviceConnectionConnect(serviceConnectionConnectRequest);
    }
}
