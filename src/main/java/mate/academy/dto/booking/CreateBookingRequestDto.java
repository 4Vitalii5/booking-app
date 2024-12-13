package mate.academy.dto.booking;

import java.time.LocalDate;

public record CreateBookingRequestDto(
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId
) {
}
