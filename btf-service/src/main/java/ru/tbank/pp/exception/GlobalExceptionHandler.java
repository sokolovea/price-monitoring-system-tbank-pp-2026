package ru.tbank.pp.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import ru.tbank.pp.dto.ExceptionDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleException(RuntimeException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getDescription(false));
        details.put("timestamp", LocalDateTime.now());

        var errorResponse = new ExceptionDto(ex.getMessage(), details);
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, getStatus(ex.getClass()));
    }

    private HttpStatus getStatus(Class<? extends Throwable> exceptionClass) {
        ResponseStatus responseStatus = exceptionClass.getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}