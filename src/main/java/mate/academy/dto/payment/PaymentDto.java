package mate.academy.dto.payment;

import java.math.BigDecimal;

public record PaymentDto(
        Long id,
        BigDecimal amount,
        String sessionUrl,
        String status
) {
}
