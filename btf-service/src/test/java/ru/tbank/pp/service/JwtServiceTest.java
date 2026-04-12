package ru.tbank.pp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret;
    private long expiration;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        secret = Base64.getEncoder().encodeToString("my-very-secret-key-that-is-at-least-256-bits-long-for-hmac-sha!".getBytes());
        expiration = 3600000; // 1 hour

        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);

        userDetails = User.builder()
                .username("test@test.com")
                .password("encodedPassword")
                .roles("USER")
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_WithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", 1L);
        extraClaims.put("role", "USER");

        String token = jwtService.generateToken(extraClaims, userDetails);

        assertThat(token).isNotNull();
        Long userId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("test@test.com");
    }

    @Test
    void extractClaim_Success() {
        String token = jwtService.generateToken(userDetails);

        String subject = jwtService.extractClaim(token, Claims::getSubject);

        assertThat(subject).isEqualTo("test@test.com");
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_WrongUser_ReturnsFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = User.builder()
                .username("other@test.com")
                .password("password")
                .roles("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenValid_ExpiredToken_ThrowsException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000);
        String token = jwtService.generateToken(userDetails);

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(Exception.class);
    }

    @Test
    void isTokenValid_InvalidToken_ReturnsFalse() {
        assertThatThrownBy(() -> jwtService.isTokenValid("invalid.token.value", userDetails))
                .isInstanceOf(Exception.class);
    }
}
