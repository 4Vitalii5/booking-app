package mate.academy.service;

import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;

public interface PaymentService {
    PaymentDto getPaymentInfo(Long userId, Long currentUserId);

    PaymentDto createPayment(CreatePaymentRequestDto requestDto);

    PaymentDto handleSuccessPayment(String sessionId);

    PaymentDto handleCancelledPayment(String sessionId);
}
