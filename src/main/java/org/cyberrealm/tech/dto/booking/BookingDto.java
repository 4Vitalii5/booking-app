package org.cyberrealm.tech.dto.booking;

import java.time.LocalDate;
import org.cyberrealm.tech.model.Booking;

public record BookingDto(
        Long id,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Long accommodationId,
        Booking.BookingStatus status
) {
}
