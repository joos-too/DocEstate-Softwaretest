package de.docestate.softwaretest.utils;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(Long id) {
        super("Property with ID " + id + " was not found.");
    }
}
