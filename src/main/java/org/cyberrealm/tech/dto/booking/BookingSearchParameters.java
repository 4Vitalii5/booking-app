package org.cyberrealm.tech.dto.booking;

public record BookingSearchParameters(
        String[] status,
        String[] userId
) {
}
