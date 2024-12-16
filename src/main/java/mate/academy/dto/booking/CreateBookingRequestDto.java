package mate.academy.dto.booking;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import mate.academy.validation.FieldsDatesValid;
import mate.academy.validation.FutureOrPresent;

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
        @Min(1)
        Long accommodationId
) {
}
