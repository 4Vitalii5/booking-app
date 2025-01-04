package org.cyberrealm.tech.dto.payment;

import java.math.BigDecimal;
import org.cyberrealm.tech.model.Payment;

public record PaymentWithoutSessionDto(
        Long bookingId,
        Payment.PaymentStatus status,
        BigDecimal amount
) {
}
