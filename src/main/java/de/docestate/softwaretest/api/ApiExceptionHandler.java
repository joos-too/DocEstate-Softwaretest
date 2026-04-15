package de.docestate.softwaretest.api;

import de.docestate.softwaretest.utils.PropertyNotFoundException;
import java.time.Instant;
import java.util.LinkedHashMap;
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
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), toValidationMessage(error.getField()))
        );

        String message = fieldErrors.values().stream()
                .findFirst()
                .orElse("Request is invalid.");

        return Map.of(
                "timestamp", Instant.now().toString(),
                "message", message,
                "fieldErrors", fieldErrors
        );
    }

    private String toValidationMessage(String fieldName) {
        return switch (fieldName) {
            case "address.postalCode" -> "Postal code must contain exactly 5 digits.";
            case "constructionYear" -> "Construction year must contain digits only.";
            case "lotSize" -> "Lot size must be greater than 0.";
            case "livingSpace" -> "Living space must be greater than 0.";
            default -> toDisplayFieldName(fieldName) + " is invalid.";
        };
    }

    private String toDisplayFieldName(String fieldName) {
        return switch (fieldName) {
            case "name" -> "Name";
            case "objectType" -> "Object type";
            case "constructionYear" -> "Construction year";
            case "lotSize" -> "Lot size";
            case "livingSpace" -> "Living space";
            case "address" -> "Address";
            case "address.city" -> "City";
            case "address.postalCode" -> "Postal code";
            case "address.street" -> "Street";
            case "address.houseNumber" -> "House number";
            default -> fieldName;
        };
    }
}
