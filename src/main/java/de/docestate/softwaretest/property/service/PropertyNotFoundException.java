package de.docestate.softwaretest.property.service;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(Long id) {
        super("Immobilie mit ID " + id + " wurde nicht gefunden.");
    }
}
