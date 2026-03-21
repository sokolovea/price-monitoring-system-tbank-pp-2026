package ru.tbank.pp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.pp.dto.AuthResponse;
import ru.tbank.pp.dto.LoginUserRequest;
import ru.tbank.pp.dto.RegisterUserRequest;
import ru.tbank.pp.entity.User;
import ru.tbank.pp.service.JwtService;
import ru.tbank.pp.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterUserRequest request) {
        if (request.getEmail() == null || request.getPassword() == null ||
                request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        User user = userService.register(request);
        String token = jwtService.generateToken(user);

        return ResponseEntity.status(201).body(new AuthResponse(user.getId(), user.getEmail(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginUserRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getEmail(), token));
    }
}