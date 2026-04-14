package de.docestate.softwaretest.property.api.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.property.api.adress.AddressResponse;

public record PropertyResponse(
        Long id,
        @JsonProperty("bezeichnung") String name,
        @JsonProperty("adresse") AddressResponse address
) {
}
