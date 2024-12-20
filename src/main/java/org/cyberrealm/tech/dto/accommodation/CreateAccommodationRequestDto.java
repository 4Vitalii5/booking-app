package org.cyberrealm.tech.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import org.cyberrealm.tech.dto.address.CreateAddressRequestDto;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.validation.ValidEnum;

public record CreateAccommodationRequestDto(
        @NotBlank
        @ValidEnum(enumClass = Accommodation.Type.class, message = "Invalid type value")
        String type,
        @NotNull
        CreateAddressRequestDto addressDto,
        @NotBlank
        String size,
        @NotEmpty
        List<String> amenities,
        @NotNull
        @Positive
        BigDecimal dailyRate,
        @NotNull
        @Positive
        Integer availability
) {
}
