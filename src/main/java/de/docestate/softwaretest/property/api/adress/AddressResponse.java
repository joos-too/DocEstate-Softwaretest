package de.docestate.softwaretest.property.api.adress;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressResponse(
        @JsonProperty("ort") String city,
        @JsonProperty("postleitzahl") String postalCode,
        @JsonProperty("strasse") String street,
        @JsonProperty("hausnummer") String houseNumber
) {
}
