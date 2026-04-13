package ru.tbank.pp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import ru.tbank.pp.dto.ExceptionDto;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleException_RuntimeException_ReturnsInternalServerError() {
        RuntimeException ex = new RuntimeException("Test error message");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        var response = exceptionHandler.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Test error message");
        assertThat(response.getBody().getInfo()).containsKey("path");
        assertThat(response.getBody().getInfo()).containsKey("timestamp");
    }

    @Test
    void handleException_ProductNotFoundException_ReturnsNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        var response = exceptionHandler.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Product not found");
    }

    @Test
    void handleException_UserNotFoundException_ReturnsNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        var response = exceptionHandler.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleException_AuthException_ReturnsUnauthorized() {
        AuthException ex = new AuthException("Authentication failed");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        var response = exceptionHandler.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication failed");
    }

    @Test
    void handleException_YandexGptResponseNotFoundException_ReturnsNotFound() {
        YandexGptResponseNotFoundException ex = new YandexGptResponseNotFoundException("GPT response not found");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        var response = exceptionHandler.handleException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("GPT response not found");
    }
}
