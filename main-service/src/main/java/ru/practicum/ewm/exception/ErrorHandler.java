package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> notFound(final NotFoundException ex) {
        log.warn("ErrorHandler: Not Found. Message: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getMessage());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> conflictException(final ConflictException ex) {
        log.warn("ErrorHandler: Conflict. Message: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getMessage());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> parameterNotValid(final ValidationException ex) {
        log.warn("ErrorHandler: Bad Request. Message: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getMessage());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.warn("ErrorHandler: Параметр {} обязателен", ex.getParameterName());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", "Параметр " + ex.getParameterName() + " обязателен");
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidateRequestBody(HandlerMethodValidationException ex) {
        log.warn("ErrorHandler: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getBody());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidateRequestBodyEmail(MethodArgumentNotValidException ex) {
        log.warn("ErrorHandler: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getBody());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleValidateUniqueName(DataIntegrityViolationException ex) {
        log.warn("ErrorHandler: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", ex.getStackTrace());
        response.put("message", ex.getLocalizedMessage());
        response.put("reason", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RuntimeException exception(final Exception ex) {
        log.error("Unexpected error occurred. Message: {}", ex.getMessage(), ex);
        return new RuntimeException("Произошла непредвиденная ошибка. " + ex.getMessage());
    }
}
