package org.cyberrealm.tech.dto.stripe;

import java.math.BigDecimal;

public record DescriptionForStripeDto(
        Long bookingId,
        BigDecimal total,
        String description
) {
}
