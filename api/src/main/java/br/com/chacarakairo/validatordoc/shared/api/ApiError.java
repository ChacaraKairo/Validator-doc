package br.com.chacarakairo.validatordoc.shared.api;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        List<FieldError> fields
) {
    public record FieldError(String field, String message) {
    }
}
