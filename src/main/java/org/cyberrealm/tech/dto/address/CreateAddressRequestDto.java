package org.cyberrealm.tech.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAddressRequestDto(
        @NotBlank
        @Pattern(regexp = "^[A-Z][a-z]+( [A-Z][a-z]+)*$")
        String country,
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$")
        String city,
        @NotBlank
        @Pattern(regexp = "^[A-Z][a-z]+( [A-Z][a-z]+)*$")
        String state,
        @NotBlank
        @Pattern(regexp = "^[A-Z][a-z]+( [A-Z][a-z]+)*$")
        String street,
        @NotBlank
        @Pattern(regexp = "[1-9]\\d*(\\s*[-/]\\s*[1-9]\\d*)?(\\s?[a-zA-Z])?")
        String houseNumber,
        @NotBlank
        @Pattern(regexp = "^\\d{5}$")
        String postalCode
) {
}
