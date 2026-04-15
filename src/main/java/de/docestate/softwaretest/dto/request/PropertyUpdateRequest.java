package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public record PropertyUpdateRequest(
        @JsonProperty("name") String name,
        @JsonProperty("address") @Valid AddressUpdateRequest address
) {
}
