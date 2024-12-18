package mate.academy.service;

import java.util.List;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentWithoutSessionDto;
import mate.academy.model.User;

public interface PaymentService {
    List<PaymentWithoutSessionDto> getPaymentInfo(Long userId, User currentUser);

    PaymentDto createPayment(CreatePaymentRequestDto requestDto);

    PaymentWithoutSessionDto handleSuccessPayment(String sessionId);

    BookingDto handleCancelledPayment(String sessionId);

    PaymentDto renewPaymentSession(Long paymentId, User currentUser);
}
