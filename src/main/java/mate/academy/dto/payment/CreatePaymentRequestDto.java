package mate.academy.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreatePaymentRequestDto(
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        @Positive
        Long bookingId
) {
}
