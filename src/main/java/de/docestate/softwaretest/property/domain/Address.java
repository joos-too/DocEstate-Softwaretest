package de.docestate.softwaretest.property.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "ort", nullable = false)
    private String city;

    @Column(name = "postleitzahl", nullable = false)
    private String postalCode;

    @Column(name = "strasse", nullable = false)
    private String street;

    @Column(name = "hausnummer", nullable = false)
    private String houseNumber;
}
