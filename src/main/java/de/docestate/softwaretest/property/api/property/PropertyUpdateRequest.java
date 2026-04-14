package de.docestate.softwaretest.property.api.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.property.api.adress.AddressUpdateRequest;
import jakarta.validation.Valid;

public record PropertyUpdateRequest(
        @JsonProperty("bezeichnung") String name,
        @JsonProperty("adresse") @Valid AddressUpdateRequest address
) {
}
