package ru.tbank.pp.integration.provider.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProviderCommunicationException extends RuntimeException {
    public ProviderCommunicationException(String message) {
        super(message);
    }
}
