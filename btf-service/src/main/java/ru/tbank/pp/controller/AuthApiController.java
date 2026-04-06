package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.api.AuthApi;                    // сгенерированный интерфейс
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.model.AuthenticationAuthRequest;   // AuthRequest
import ru.tbank.pp.model.AuthenticationAuthResponse;  // AuthResponse
import ru.tbank.pp.entity.User;
import ru.tbank.pp.service.JwtService;
import ru.tbank.pp.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * POST /auth/register
     */
    @Override
    public ResponseEntity<AuthenticationAuthResponse> authenticationRegister(
            AuthenticationAuthRequest authRequest) {

        if (authRequest.getEmail() == null || authRequest.getPassword() == null ||
                authRequest.getEmail().isBlank() || authRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        User user = userService.register(convertToRegisterRequest(authRequest));

        String token = jwtService.generateToken(user);

        AuthenticationAuthResponse response = new AuthenticationAuthResponse()
                .email(user.getEmail())
                .token(token);

        return ResponseEntity.status(201).body(response);
    }

    /**
     * POST /auth/login
     */
    @Override
    public ResponseEntity<AuthenticationAuthResponse> authenticationLogin(
            AuthenticationAuthRequest authRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        AuthenticationAuthResponse response = new AuthenticationAuthResponse()
                .email(user.getEmail())
                .token(token);

        return ResponseEntity.ok(response);
    }

    private RegisterUserRequest convertToRegisterRequest(
            AuthenticationAuthRequest authRequest) {

        RegisterUserRequest req = new ru.tbank.pp.dto.RegisterUserRequest(authRequest.getEmail(),
                authRequest.getPassword());
        return req;
    }
}