package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.ServiceApi;
import ru.tbank.pp.model.ServiceConnectionConnectRequest;
import ru.tbank.pp.model.ServiceConnectionStatusCheckRequest;
import ru.tbank.pp.service.UserNotificationService;

@RestController
@RequiredArgsConstructor
public class ServiceConnectionApiController implements ServiceApi {
    private final UserNotificationService userNotificationService;

    @Override
    public ResponseEntity<Boolean> serviceConnectionCheckStatus(ServiceConnectionStatusCheckRequest serviceConnectionStatusCheckRequest) {
        return ResponseEntity.ok(userNotificationService.checkConnectionStatus(serviceConnectionStatusCheckRequest));
    }

    @Override
    public ResponseEntity<Void> serviceConnectionConnect(ServiceConnectionConnectRequest serviceConnectionConnectRequest) {
        userNotificationService.connectService(serviceConnectionConnectRequest);
        return ResponseEntity.ok().build();
    }
}
