package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.CANNOT_CREATE_SESSION;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_STRIPE_SESSIONS;
import static org.cyberrealm.tech.util.TestConstants.NOT_FOUND_EXPIRED_PAYMENTS;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestUtil.API_EXCEPTION;
import static org.cyberrealm.tech.util.TestUtil.DESCRIPTION_FOR_STRIPE_DTO;
import static org.cyberrealm.tech.util.TestUtil.SESSION;
import static org.cyberrealm.tech.util.TestUtil.SESSION_COLLECTION;
import static org.cyberrealm.tech.util.TestUtil.getExpiredPayment;
import static org.cyberrealm.tech.util.TestUtil.getSecondBooking;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.exception.StripeProcessingException;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private StripeService stripeService;

    @Test
    @DisplayName("Create stripe session with valid data")
    void createStripeSession_validStripeDto_returnsSession() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            //Given
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(SESSION);

            //When
            Session actualSession = stripeService.createStripeSession(DESCRIPTION_FOR_STRIPE_DTO);

            //Then
            assertThat(actualSession).isNotNull();
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    @DisplayName("Throw StripeProcessingException during data processing")
    void createStripeSession_stripeException_throwsStripeProcessingException() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            //Given
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(API_EXCEPTION);

            //When
            StripeProcessingException exception = assertThrows(StripeProcessingException.class,
                    () -> stripeService.createStripeSession(DESCRIPTION_FOR_STRIPE_DTO));
            String actual = exception.getMessage();

            //Then
            String expected = CANNOT_CREATE_SESSION;
            assertThat(actual).isEqualTo(expected);
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    @DisplayName("Renew session link for expired payment")
    void renewSession_expiredPayment_returnsSession() throws MalformedURLException {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            //Given
            Payment payment = getExpiredPayment();
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(SESSION);

            //When
            Session actualSession = stripeService.renewSession(payment);

            //Then
            assertThat(actualSession).isNotNull();
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    @DisplayName("Throw StripeProcessingException expired payment does`t exists")
    void renewSession_nonExpiredPayment_throwsStripeProcessingException()
            throws MalformedURLException {
        //Given
        Payment payment = getExpiredPayment();
        payment.setStatus(Payment.PaymentStatus.PAID);

        //When
        StripeProcessingException exception = assertThrows(StripeProcessingException.class, () ->
                stripeService.renewSession(payment));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(NOT_FOUND_EXPIRED_PAYMENTS, payment.getId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Handle expired sessions")
    void checkExpiredSession_validParams_handlesExpiredSessions() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            //Given
            SESSION.setId(SESSION_ID);
            SESSION.setStatus(EXPIRED);
            SESSION_COLLECTION.setData(List.of(SESSION));
            Booking booking = getSecondBooking();
            mockedSession.when(() -> Session.list(any(SessionListParams.class)))
                    .thenReturn(SESSION_COLLECTION);
            when(paymentRepository.existsBySessionId(SESSION_ID)).thenReturn(true);
            when(paymentRepository.findBookingBySessionId(anyString()))
                    .thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            // When
            stripeService.checkExpiredSession();

            //Then
            verify(paymentRepository, times(1))
                    .existsBySessionId(anyString());
            verify(paymentRepository, times(1))
                    .findBookingBySessionId(anyString());
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }
    }

    @Test
    @DisplayName("Throw StripeProcessingException during checking expired sessions")
    void checkExpiredSession_stripeException_throwsStripeProcessingException() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            //Given
            mockedSession.when(() -> Session.list(any(SessionListParams.class)))
                    .thenThrow(API_EXCEPTION);

            //When
            StripeProcessingException exception = assertThrows(StripeProcessingException.class,
                    () -> stripeService.checkExpiredSession());
            String actual = exception.getMessage();

            //Then
            String expected = EXPIRED_STRIPE_SESSIONS;
            assertThat(actual).isEqualTo(expected);
        }
    }
}
