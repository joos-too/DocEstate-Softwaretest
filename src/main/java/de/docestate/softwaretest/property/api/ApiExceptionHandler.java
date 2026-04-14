package de.docestate.softwaretest.property.api;

import de.docestate.softwaretest.property.service.PropertyNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PropertyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(PropertyNotFoundException exception) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> toDisplayFieldName(error.getField()) + " ist ungültig.")
                .orElse("Anfrage ist ungültig.");

        return Map.of(
                "timestamp", Instant.now().toString(),
                "message", message
        );
    }

    private String toDisplayFieldName(String fieldName) {
        return switch (fieldName) {
            case "name" -> "Bezeichnung";
            case "address" -> "Adresse";
            case "city" -> "Ort";
            case "postalCode" -> "Postleitzahl";
            case "street" -> "Straße";
            case "houseNumber" -> "Hausnummer";
            default -> fieldName;
        };
    }
}
