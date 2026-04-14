package de.docestate.softwaretest.property.api.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.property.api.adress.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PropertyRequest(
        @JsonProperty("bezeichnung") @NotBlank String name,
        @JsonProperty("adresse") @Valid @NotNull AddressRequest address
) {
}
