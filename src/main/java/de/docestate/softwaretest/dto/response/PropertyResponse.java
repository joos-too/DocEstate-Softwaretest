package de.docestate.softwaretest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PropertyResponse(
        Long id,
        @JsonProperty("name") String name,
        @JsonProperty("address") AddressResponse address
) {
}
