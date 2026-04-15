package de.docestate.softwaretest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @JsonProperty("city") @NotBlank String city,
        @JsonProperty("postalCode") @NotBlank @Pattern(regexp = "\\d{5}") String postalCode,
        @JsonProperty("street") @NotBlank String street,
        @JsonProperty("houseNumber") @NotBlank String houseNumber
) {
}
