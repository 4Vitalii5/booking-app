package mate.academy.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public static final String SUCCESS_URL = "http://localhost:8080/api/payments/success/";
    public static final String CANCEL_URL = "http://localhost:8080/api/payments/cancel/";
    public static final long DEFAULT_QUANTITY = 1L;
    public static final String DEFAULT_CURRENCY = "usd";
    public static final BigDecimal CENTS_AMOUNT = BigDecimal.valueOf(100);
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Session createStripeSession(CreatePaymentRequestDto requestDto) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
                                .setPriceData(SessionCreateParams.LineItem.PriceData
                                        .builder()
                                        .setCurrency(DEFAULT_CURRENCY)
                                        .setUnitAmountDecimal(
                                                requestDto.amount().multiply(CENTS_AMOUNT)
                                        )
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder()
                                                        .setName(
                                                                "Booking #"
                                                                        + requestDto.bookingId())
                                                        .build()
                                        )
                                        .build()
                                )
                                .build()
                )
                .build();
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Cannot create session", e);
        }
    }
}
