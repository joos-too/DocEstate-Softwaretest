package de.docestate.softwaretest.property.api.adress;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @JsonProperty("ort") @NotBlank String city,
        @JsonProperty("postleitzahl") @NotBlank String postalCode,
        @JsonProperty("strasse") @NotBlank String street,
        @JsonProperty("hausnummer") @NotBlank String houseNumber
) {
}
