package org.cyberrealm.tech.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.stripe.DescriptionForStripeDto;
import org.cyberrealm.tech.exception.PaymentProcessingException;
import org.cyberrealm.tech.exception.StripeProcessingException;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeService {
    public static final long DEFAULT_QUANTITY = 1L;
    public static final String DEFAULT_CURRENCY = "usd";
    public static final BigDecimal CENTS_AMOUNT = BigDecimal.valueOf(100);
    public static final long MAX_QUANTITY_OF_RECORDS = 100L;
    public static final String EXPIRED_STATUS = "expired";
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;
    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Session createStripeSession(DescriptionForStripeDto stripeDto) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
                                .setPriceData(SessionCreateParams.LineItem.PriceData
                                        .builder()
                                        .setCurrency(DEFAULT_CURRENCY)
                                        .setUnitAmountDecimal(
                                                stripeDto.total().multiply(CENTS_AMOUNT))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder()
                                                        .setName("Booking #"
                                                                        + stripeDto.bookingId())
                                                        .setDescription(stripeDto.description())
                                                        .build())
                                        .build())
                                .build())
                .build();
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeProcessingException("Cannot create session", e);
        }
    }

    public Session renewSession(Payment payment) {
        if (payment.getStatus() == Payment.PaymentStatus.EXPIRED) {
            DescriptionForStripeDto stripeDto = new DescriptionForStripeDto(
                    payment.getBooking().getId(),
                    payment.getAmountToPay(),
                    "Renewal for booking #" + payment.getBooking().getId());
            return createStripeSession(stripeDto);
        } else {
            throw new StripeProcessingException("Cannot renew session for payment with id:"
                    + payment.getId() + ". Which has not expired.");
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void checkExpiredSession() {
        SessionListParams params = SessionListParams.builder().setLimit(MAX_QUANTITY_OF_RECORDS)
                .build();
        try {
            List<Session> expiredSessions = getExpiredSessions(params);
            if (!expiredSessions.isEmpty()) {
                handleExpiredSessions(expiredSessions);
            }
        } catch (StripeException e) {
            throw new StripeProcessingException("Can't check expired Stripe sessions.", e);
        }
    }

    private void handleExpiredSessions(List<Session> expiredSessions) {
        expiredSessions.forEach(session -> {
            Booking booking = getBookingBySessionId(session.getId());
            if (!Booking.BookingStatus.EXPIRED.equals(booking.getStatus())) {
                booking.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(booking);
            }
        });
    }

    private List<Session> getExpiredSessions(SessionListParams params) throws StripeException {
        return Session.list(params).getData().stream()
                .filter(session -> session.getStatus().equals(EXPIRED_STATUS))
                .filter(session -> paymentRepository.existsBySessionId(session.getId()))
                .toList();
    }

    private Booking getBookingBySessionId(String sessionId) {
        return paymentRepository.findBookingBySessionId(sessionId).orElseThrow(() ->
                new PaymentProcessingException("Can't find booking for payment by sessionId:"
                        + sessionId));
    }
}
