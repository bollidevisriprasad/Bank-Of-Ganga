
package com.dspbank.exception;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, String>> api(ApiException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Collections.singletonMap("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> invalid(
            MethodArgumentNotValidException e) {

        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request.");

        return ResponseEntity
                .badRequest()
                .body(Collections.singletonMap("error", msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> unreadable(
            HttpMessageNotReadableException e) {

        return ResponseEntity
                .badRequest()
                .body(Collections.singletonMap(
                        "error",
                        "Request body is missing or malformed."
                ));
    }
}
