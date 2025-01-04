package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.CHECK_IN_DATE;
import static org.cyberrealm.tech.util.TestConstants.DAILY_RATE_23;
import static org.cyberrealm.tech.util.TestConstants.ELECTRICITY;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_AVAILABILITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_AMOUNT;
import static org.cyberrealm.tech.util.TestConstants.POOL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SESSION_URL;
import static org.cyberrealm.tech.util.TestConstants.STUDIO;
import static org.cyberrealm.tech.util.TestConstants.WIFI;
import static org.cyberrealm.tech.util.TestUtil.AMENITIES;
import static org.cyberrealm.tech.util.TestUtil.API_EXCEPTION;
import static org.cyberrealm.tech.util.TestUtil.DESCRIPTION_FOR_STRIPE_DTO;
import static org.cyberrealm.tech.util.TestUtil.EXPIRED_PAYMENT;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ACCOMMODATION;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ADDRESS;
import static org.cyberrealm.tech.util.TestUtil.FIRST_USER;
import static org.cyberrealm.tech.util.TestUtil.MANAGER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.SECOND_BOOKING;
import static org.cyberrealm.tech.util.TestUtil.SESSION;
import static org.cyberrealm.tech.util.TestUtil.SESSION_COLLECTION;
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
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.exception.StripeProcessingException;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() throws MalformedURLException {
        MANAGER_ROLE.setRole(Role.RoleName.ROLE_MANAGER);

        FIRST_USER.setId(FIRST_USER_ID);
        FIRST_USER.setFirstName(FIRST_USER_FIRST_NAME);
        FIRST_USER.setLastName(FIRST_USER_LAST_NAME);
        FIRST_USER.setEmail(FIRST_USER_EMAIL);
        FIRST_USER.setPassword(FIRST_USER_PASSWORD);
        FIRST_USER.setRoles(Set.of(MANAGER_ROLE));

        AMENITIES.add(POOL);
        AMENITIES.add(ELECTRICITY);
        AMENITIES.add(WIFI);

        FIRST_ACCOMMODATION.setId(FIRST_ACCOMMODATION_ID);
        FIRST_ACCOMMODATION.setType(Accommodation.Type.HOUSE);
        FIRST_ACCOMMODATION.setAddress(FIRST_ADDRESS);
        FIRST_ACCOMMODATION.setSize(STUDIO);
        FIRST_ACCOMMODATION.setAmenities(AMENITIES);
        FIRST_ACCOMMODATION.setDailyRate(DAILY_RATE_23);
        FIRST_ACCOMMODATION.setAvailability(FIRST_AVAILABILITY);

        SECOND_BOOKING.setId(SECOND_BOOKING_ID);
        SECOND_BOOKING.setCheckInDate(CHECK_IN_DATE);
        SECOND_BOOKING.setCheckOutDate(SECOND_CHECK_OUT_DATE);
        SECOND_BOOKING.setAccommodation(FIRST_ACCOMMODATION);
        SECOND_BOOKING.setUser(FIRST_USER);
        SECOND_BOOKING.setStatus(Booking.BookingStatus.PENDING);

        EXPIRED_PAYMENT.setId(EXPIRED_PAYMENT_ID);
        EXPIRED_PAYMENT.setBooking(SECOND_BOOKING);
        EXPIRED_PAYMENT.setAmountToPay(PAYMENT_AMOUNT);
        EXPIRED_PAYMENT.setSessionId(EXPIRED_SESSION_ID);
        EXPIRED_PAYMENT.setSessionUrl(new URL(SESSION_URL));
        EXPIRED_PAYMENT.setStatus(Payment.PaymentStatus.EXPIRED);

        SESSION.setId(SESSION_ID);
        SESSION.setStatus(EXPIRED);
        SESSION_COLLECTION.setData(List.of(SESSION));
    }

    @Test
    void createStripeSession_validStripeDto_returnsSession() {
        Session session = new Session();
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(session);

            Session actualSession = stripeService.createStripeSession(DESCRIPTION_FOR_STRIPE_DTO);

            assertThat(actualSession).isNotNull();
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    void createStripeSession_stripeException_throwsStripeProcessingException() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(API_EXCEPTION);

            assertThrows(StripeProcessingException.class, () ->
                    stripeService.createStripeSession(DESCRIPTION_FOR_STRIPE_DTO));

            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    void renewSession_expiredPayment_returnsSession() {
        Session session = new Session();
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(session);

            Session actualSession = stripeService.renewSession(EXPIRED_PAYMENT);

            assertThat(actualSession).isNotNull();
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)),
                    times(1));
        }
    }

    @Test
    void renewSession_nonExpiredPayment_throwsStripeProcessingException() {
        EXPIRED_PAYMENT.setStatus(Payment.PaymentStatus.PAID);

        assertThrows(StripeProcessingException.class, () ->
                stripeService.renewSession(EXPIRED_PAYMENT));
    }

    @Test
    void checkExpiredSession_validParams_handlesExpiredSessions() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.list(any(SessionListParams.class)))
                    .thenReturn(SESSION_COLLECTION);
            when(paymentRepository.existsBySessionId(SESSION_ID)).thenReturn(true);
            when(paymentRepository.findBookingBySessionId(anyString()))
                    .thenReturn(Optional.of(SECOND_BOOKING));
            when(bookingRepository.save(any(Booking.class))).thenReturn(SECOND_BOOKING);

            stripeService.checkExpiredSession();

            verify(paymentRepository, times(1))
                    .existsBySessionId(anyString());
            verify(paymentRepository, times(1))
                    .findBookingBySessionId(anyString());
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }
    }

    @Test
    void checkExpiredSession_stripeException_throwsStripeProcessingException() {
        try (MockedStatic<Session> mockedSession = Mockito.mockStatic(Session.class)) {
            mockedSession.when(() -> Session.list(any(SessionListParams.class)))
                    .thenThrow(API_EXCEPTION);

            assertThrows(StripeProcessingException.class, () ->
                    stripeService.checkExpiredSession());
        }
    }
}
