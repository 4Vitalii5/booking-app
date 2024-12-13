package mate.academy.dto.booking;

import java.time.LocalDate;

public record BookingDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        String status
) {
}
