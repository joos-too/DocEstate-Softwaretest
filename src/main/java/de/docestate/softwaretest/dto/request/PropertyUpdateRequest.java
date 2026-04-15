package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.domain.PropertyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record PropertyUpdateRequest(
        @JsonProperty("name") String name,
        @JsonProperty("objectType") PropertyType objectType,
        @JsonProperty("constructionYear") Integer constructionYear,
        @JsonProperty("lotSize") @Positive Float lotSize,
        @JsonProperty("livingSpace") @Positive Float livingSpace,
        @JsonProperty("address") @Valid AddressUpdateRequest address
) {
}
