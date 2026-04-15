package de.docestate.softwaretest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType objectType;

    @Column(nullable = false)
    private Integer constructionYear;

    @Column(nullable = false)
    private Float lotSize;

    @Column(nullable = false)
    private Float livingSpace;

    @Embedded
    private Address address;

    public void update(
            String name,
            PropertyType objectType,
            Integer constructionYear,
            Float lotSize,
            Float livingSpace,
            Address address
    ) {
        this.name = name;
        this.objectType = objectType;
        this.constructionYear = constructionYear;
        this.lotSize = lotSize;
        this.livingSpace = livingSpace;
        this.address = address;
    }
}
