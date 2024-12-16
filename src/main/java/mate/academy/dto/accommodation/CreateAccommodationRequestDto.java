package mate.academy.dto.accommodation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.dto.address.CreateAddressRequestDto;
import mate.academy.model.Accommodation;
import mate.academy.validation.ValidEnum;

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
        @Min(1)
        BigDecimal dailyRate,
        @NotNull
        @Min(0)
        Integer availability
) {
}
