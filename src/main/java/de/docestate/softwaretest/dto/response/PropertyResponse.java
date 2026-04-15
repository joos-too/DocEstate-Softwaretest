package de.docestate.softwaretest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.domain.PropertyType;

public record PropertyResponse(
        Long id,
        @JsonProperty("name") String name,
        @JsonProperty("objectType") PropertyType objectType,
        @JsonProperty("constructionYear") String constructionYear,
        @JsonProperty("lotSize") Float lotSize,
        @JsonProperty("livingSpace") Float livingSpace,
        @JsonProperty("address") AddressResponse address
) {
}
