package org.cyberrealm.tech.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.cyberrealm.tech.validation.FieldsDatesValid;
import org.cyberrealm.tech.validation.FutureOrPresent;

@FieldsDatesValid.List({
        @FieldsDatesValid(
                field = "checkInDate",
                fieldMatch = "checkOutDate",
                message = "CheckOutDate can't be before or equal checkInDate!"
        )
})
public record CreateBookingRequestDto(
        @FutureOrPresent
        LocalDate checkInDate,
        LocalDate checkOutDate,
        @NotNull
        @Positive
        Long accommodationId
) {
}
