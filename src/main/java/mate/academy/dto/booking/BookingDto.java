package mate.academy.dto.booking;

import java.time.LocalDate;
import mate.academy.model.Booking;

public record BookingDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Booking.BookingStatus status
) {
}
