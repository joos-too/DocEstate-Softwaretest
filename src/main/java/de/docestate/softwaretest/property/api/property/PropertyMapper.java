package de.docestate.softwaretest.property.api.property;

import de.docestate.softwaretest.property.api.adress.AddressRequest;
import de.docestate.softwaretest.property.api.adress.AddressResponse;
import de.docestate.softwaretest.property.api.adress.AddressUpdateRequest;
import de.docestate.softwaretest.property.domain.Address;
import de.docestate.softwaretest.property.domain.Property;

public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static Property toEntity(PropertyRequest request) {
        return Property.builder()
                .name(request.name())
                .address(toAddress(request.address()))
                .build();
    }

    public static Address toAddress(AddressRequest request) {
        return Address.builder()
                .city(request.city())
                .postalCode(request.postalCode())
                .street(request.street())
                .houseNumber(request.houseNumber())
                .build();
    }

    public static Address mergeAddress(Address currentAddress, AddressUpdateRequest request) {
        if (request == null) {
            return currentAddress;
        }

        return Address.builder()
                .city(request.city() != null ? request.city() : currentAddress.getCity())
                .postalCode(request.postalCode() != null ? request.postalCode() : currentAddress.getPostalCode())
                .street(request.street() != null ? request.street() : currentAddress.getStreet())
                .houseNumber(request.houseNumber() != null ? request.houseNumber() : currentAddress.getHouseNumber())
                .build();
    }

    public static PropertyResponse toResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getName(),
                new AddressResponse(
                        property.getAddress().getCity(),
                        property.getAddress().getPostalCode(),
                        property.getAddress().getStreet(),
                        property.getAddress().getHouseNumber()
                )
        );
    }
}
