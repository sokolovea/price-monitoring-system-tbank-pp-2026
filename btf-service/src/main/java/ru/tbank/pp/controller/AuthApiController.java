package ru.tbank.pp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.AuthApi;
import ru.tbank.pp.model.AuthenticationAuthRequest;
import ru.tbank.pp.model.AuthenticationAuthResponse;

@RestController
public class AuthApiController implements AuthApi {
    @Override
    public ResponseEntity<AuthenticationAuthResponse> authenticationLogin(AuthenticationAuthRequest authRequest) {
        AuthenticationAuthResponse response = new AuthenticationAuthResponse()
                .email(authRequest.getEmail())
                .token("some.jwt.token");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthenticationAuthResponse> authenticationRegister(AuthenticationAuthRequest authRequest) {
        // Регистрация пользователя
        AuthenticationAuthResponse response = new AuthenticationAuthResponse()
                .email(authRequest.getEmail())
                .token("some.jwt.token");
        return ResponseEntity.status(201).body(response);
    }
}
