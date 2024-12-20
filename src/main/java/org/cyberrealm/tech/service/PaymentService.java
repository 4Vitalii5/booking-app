package org.cyberrealm.tech.service;

import java.util.List;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.payment.CreatePaymentRequestDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.cyberrealm.tech.model.User;

public interface PaymentService {
    List<PaymentWithoutSessionDto> getPaymentInfo(Long userId, User currentUser);

    PaymentDto createPayment(CreatePaymentRequestDto requestDto);

    PaymentWithoutSessionDto handleSuccessPayment(String sessionId);

    BookingDto handleCancelledPayment(String sessionId);

    PaymentDto renewPaymentSession(Long paymentId, User currentUser);
}
