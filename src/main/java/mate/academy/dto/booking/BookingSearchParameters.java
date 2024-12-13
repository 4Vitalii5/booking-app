package mate.academy.dto.booking;

public record BookingSearchParameters(
        String[] status,
        String[] userId
) {
}
