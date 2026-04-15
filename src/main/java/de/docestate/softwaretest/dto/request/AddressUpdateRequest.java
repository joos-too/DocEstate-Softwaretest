package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record AddressUpdateRequest(
        @JsonProperty("city") String city,
        @JsonProperty("postalCode") @Pattern(regexp = "\\d{5}") String postalCode,
        @JsonProperty("street") String street,
        @JsonProperty("houseNumber") String houseNumber
) {
}
