package br.com.chacarakairo.validatordoc.shared.api;

import br.com.chacarakairo.validatordoc.session.DocumentSessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(DocumentSessionNotFoundException exception) {
        return new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "DOCUMENT_SESSION_NOT_FOUND",
                exception.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException exception) {
        List<ApiError.FieldError> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        return new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "A requisição contém campos inválidos.",
                fields
        );
    }
}
