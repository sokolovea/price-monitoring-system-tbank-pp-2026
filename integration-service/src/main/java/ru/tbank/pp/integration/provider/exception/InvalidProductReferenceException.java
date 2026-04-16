package ru.tbank.pp.integration.provider.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidProductReferenceException extends RuntimeException {
    public InvalidProductReferenceException(String message) {
        super(message);
    }
}
