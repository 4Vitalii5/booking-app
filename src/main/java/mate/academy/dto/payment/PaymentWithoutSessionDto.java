package mate.academy.dto.payment;

import java.math.BigDecimal;
import mate.academy.model.Payment;

public record PaymentWithoutSessionDto(
        Long bookingId,
        Payment.PaymentStatus status,
        BigDecimal amountToPay
) {
}
