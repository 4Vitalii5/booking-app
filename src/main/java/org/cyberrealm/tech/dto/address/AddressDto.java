package org.cyberrealm.tech.dto.address;

public record AddressDto(
        Long id,
        String country,
        String city,
        String state,
        String street,
        String houseNumber,
        String postalCode
) {
}
