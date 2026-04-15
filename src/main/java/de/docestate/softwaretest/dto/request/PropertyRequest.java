package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PropertyRequest(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("address") @Valid @NotNull AddressRequest address
) {
}
