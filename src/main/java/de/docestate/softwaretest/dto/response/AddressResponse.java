package de.docestate.softwaretest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressResponse(
        @JsonProperty("city") String city,
        @JsonProperty("postalCode") String postalCode,
        @JsonProperty("street") String street,
        @JsonProperty("houseNumber") String houseNumber
) {
}
