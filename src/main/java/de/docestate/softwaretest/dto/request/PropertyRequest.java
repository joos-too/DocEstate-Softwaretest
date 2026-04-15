package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.docestate.softwaretest.domain.PropertyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record PropertyRequest(
        @JsonProperty("name") @NotBlank String name,
        @JsonProperty("objectType") @NotNull PropertyType objectType,
        @JsonProperty("constructionYear") @NotBlank @Pattern(regexp = "\\d+") String constructionYear,
        @JsonProperty("lotSize") @NotNull @Positive Float lotSize,
        @JsonProperty("livingSpace") @NotNull @Positive Float livingSpace,
        @JsonProperty("address") @Valid @NotNull AddressRequest address
) {
}
