package mate.academy.service.impl;

import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import mate.academy.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto getPaymentInfo(Long userId, Long currentUserId) {
        return null;
    }

    @Override
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto) {
        Session session = stripeService.createStripeSession(requestDto);
        Payment payment = paymentMapper.toEntity(requestDto);
        payment.setSessionUrl(getSessionUrl(session));
        payment.setSessionId(session.getId());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentDto handleSuccessPayment(String sessionId) {
        return null;
    }

    @Override
    public PaymentDto handleCancelledPayment(String sessionId) {
        return null;
    }

    private URL getSessionUrl(Session session) {
        try {
            return new URL(session.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }
}
