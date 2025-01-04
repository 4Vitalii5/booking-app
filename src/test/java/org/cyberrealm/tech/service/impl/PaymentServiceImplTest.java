package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.AMOUNT_PAID_FIELD;
import static org.cyberrealm.tech.util.TestConstants.CHECK_IN_DATE;
import static org.cyberrealm.tech.util.TestConstants.CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.DAILY_RATE_23;
import static org.cyberrealm.tech.util.TestConstants.ELECTRICITY;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_BY_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestConstants.FIRST_AVAILABILITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.ID_FIELD;
import static org.cyberrealm.tech.util.TestConstants.NUMBER_OF_DAYS;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_AMOUNT;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_STATUS;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_STRING;
import static org.cyberrealm.tech.util.TestConstants.POOL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.SECOND_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SESSION_URL;
import static org.cyberrealm.tech.util.TestConstants.STUDIO;
import static org.cyberrealm.tech.util.TestConstants.WIFI;
import static org.cyberrealm.tech.util.TestUtil.AMENITIES;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_PAYMENT_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ACCOMMODATION;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ADDRESS;
import static org.cyberrealm.tech.util.TestUtil.FIRST_BOOKING;
import static org.cyberrealm.tech.util.TestUtil.FIRST_PAYMENT;
import static org.cyberrealm.tech.util.TestUtil.FIRST_USER;
import static org.cyberrealm.tech.util.TestUtil.MANAGER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.PAID_PAYMENT_WITHOUT_SESSION_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAYMENT_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAYMENT_WITHOUT_SESSION_DTO;
import static org.cyberrealm.tech.util.TestUtil.SECOND_BOOKING;
import static org.cyberrealm.tech.util.TestUtil.SECOND_PAYMENT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.cyberrealm.tech.dto.stripe.DescriptionForStripeDto;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.exception.PaymentProcessingException;
import org.cyberrealm.tech.mapper.BookingMapper;
import org.cyberrealm.tech.mapper.PaymentMapper;
import org.cyberrealm.tech.mapper.impl.BookingMapperImpl;
import org.cyberrealm.tech.mapper.impl.PaymentMapperImpl;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    @Mock
    private StripeService stripeService;

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private PaymentMapper paymentMapper = new PaymentMapperImpl();

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

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

        FIRST_ADDRESS.setId(FIRST_ADDRESS_ID);
        FIRST_ADDRESS.setCountry(FIRST_ADDRESS_COUNTRY);
        FIRST_ADDRESS.setCity(FIRST_ADDRESS_CITY);
        FIRST_ADDRESS.setState(FIRST_ADDRESS_STATE);
        FIRST_ADDRESS.setStreet(FIRST_ADDRESS_STREET);
        FIRST_ADDRESS.setHouseNumber(FIRST_ADDRESS_HOUSE_NUMBER);
        FIRST_ADDRESS.setPostalCode(FIRST_ADDRESS_POSTAL_CODE);

        FIRST_ACCOMMODATION.setId(FIRST_ACCOMMODATION_ID);
        FIRST_ACCOMMODATION.setType(Accommodation.Type.HOUSE);
        FIRST_ACCOMMODATION.setAddress(FIRST_ADDRESS);
        FIRST_ACCOMMODATION.setSize(STUDIO);
        FIRST_ACCOMMODATION.setAmenities(AMENITIES);
        FIRST_ACCOMMODATION.setDailyRate(DAILY_RATE_23);
        FIRST_ACCOMMODATION.setAvailability(FIRST_AVAILABILITY);

        FIRST_BOOKING.setId(FIRST_BOOKING_ID);
        FIRST_BOOKING.setCheckInDate(CHECK_IN_DATE);
        FIRST_BOOKING.setCheckOutDate(CHECK_OUT_DATE);
        FIRST_BOOKING.setAccommodation(FIRST_ACCOMMODATION);
        FIRST_BOOKING.setUser(FIRST_USER);
        FIRST_BOOKING.setStatus(Booking.BookingStatus.PENDING);

        SECOND_BOOKING.setId(SECOND_BOOKING_ID);
        SECOND_BOOKING.setCheckInDate(CHECK_IN_DATE);
        SECOND_BOOKING.setCheckOutDate(SECOND_CHECK_OUT_DATE);
        SECOND_BOOKING.setAccommodation(FIRST_ACCOMMODATION);
        SECOND_BOOKING.setUser(FIRST_USER);
        SECOND_BOOKING.setStatus(Booking.BookingStatus.PENDING);

        FIRST_PAYMENT.setId(FIRST_PAYMENT_ID);
        FIRST_PAYMENT.setBooking(FIRST_BOOKING);
        FIRST_PAYMENT.setAmountToPay(PAYMENT_AMOUNT);
        FIRST_PAYMENT.setSessionId(SESSION_ID);
        FIRST_PAYMENT.setSessionUrl(new URL(SESSION_URL));
        FIRST_PAYMENT.setStatus(PAYMENT_STATUS);

        SECOND_PAYMENT.setId(SECOND_PAYMENT_ID);
        SECOND_PAYMENT.setBooking(SECOND_BOOKING);
        SECOND_PAYMENT.setAmountToPay(PAYMENT_AMOUNT);
        SECOND_PAYMENT.setSessionId(SECOND_SESSION_ID);
        SECOND_PAYMENT.setSessionUrl(new URL(SESSION_URL));
        SECOND_PAYMENT.setStatus(PAYMENT_STATUS);
    }

    @Test
    @DisplayName("Create payment successfully")
    void createPayment_validRequestDto_returnsPaymentDto() {
        Session mockSession = mock(Session.class);
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_BOOKING));
        when(bookingRepository.numberOfDays(FIRST_BOOKING_ID))
                .thenReturn(NUMBER_OF_DAYS);
        when(mockSession.getUrl()).thenReturn(SESSION_URL);
        when(mockSession.getId()).thenReturn(SESSION_ID);
        when(stripeService.createStripeSession(any(DescriptionForStripeDto.class)))
                .thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(FIRST_PAYMENT);

        PaymentDto actual = paymentService.createPayment(CREATE_PAYMENT_REQUEST_DTO);

        PaymentDto expected = PAYMENT_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().ignoringFields(ID_FIELD)
                .isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(stripeService, times(1)).createStripeSession(any(DescriptionForStripeDto.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Handle successful payment")
    void handleSuccessPayment_validSessionId_returnsPaymentWithoutSessionDto() {
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(SECOND_PAYMENT));

        PaymentWithoutSessionDto actual = paymentService.handleSuccessPayment(SESSION_ID);

        PaymentWithoutSessionDto expected = PAID_PAYMENT_WITHOUT_SESSION_DTO;
        assertThat(actual).usingRecursiveComparison().ignoringFields(AMOUNT_PAID_FIELD)
                .isEqualTo(expected);
        assertThat(actual.amountPaid()).isEqualTo(PAYMENT_AMOUNT);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(notificationService, times(1)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Handle cancelled payment")
    void handleCancelledPayment_validSessionId_returnsBookingDto() {
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(FIRST_PAYMENT));

        BookingDto actual = paymentService.handleCancelledPayment(SESSION_ID);

        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(notificationService, times(1)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Renew payment session")
    void renewPaymentSession_validPaymentId_returnsUpdatedPaymentDto() {
        Session mockSession = mock(Session.class);
        when(paymentRepository.findById(FIRST_PAYMENT_ID)).thenReturn(Optional.of(FIRST_PAYMENT));
        when(mockSession.getUrl()).thenReturn(SESSION_URL);
        when(mockSession.getId()).thenReturn(SESSION_ID);
        when(stripeService.renewSession(FIRST_PAYMENT)).thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(FIRST_PAYMENT);

        PaymentDto actual = paymentService.renewPaymentSession(FIRST_PAYMENT_ID, FIRST_USER);

        PaymentDto expected = PAYMENT_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(paymentRepository, times(1)).findById(FIRST_PAYMENT_ID);
        verify(stripeService, times(1)).renewSession(FIRST_PAYMENT);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Get payment info by user ID")
    void getPaymentInfo_validUserId_returnsPaymentWithoutSessionDtoList() {
        when(paymentRepository.findByBookingId(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_PAYMENT));
        when(bookingRepository.findAllByUserId(FIRST_USER_ID)).thenReturn(List.of(FIRST_BOOKING));

        List<PaymentWithoutSessionDto> actual = paymentService.getPaymentInfo(FIRST_USER_ID,
                FIRST_USER);

        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(PAYMENT_WITHOUT_SESSION_DTO);
        verify(paymentRepository, times(1)).findByBookingId(FIRST_BOOKING_ID);
        verify(bookingRepository, times(1)).findAllByUserId(FIRST_USER_ID);
    }

    @Test
    @DisplayName("Get payment by session ID throws EntityNotFoundException")
    void getPayment_invalidSessionId_throwsEntityNotFoundException() {
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                paymentService.handleSuccessPayment(SESSION_ID));
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_BY_SESSION_ID, SESSION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
    }

    @Test
    @DisplayName("Renew payment session with invalid payment ID throws PaymentProcessingException")
    void renewPaymentSession_invalidPaymentId_throwsPaymentProcessingException() {
        when(paymentRepository.findById(FIRST_PAYMENT_ID)).thenReturn(Optional.empty());

        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () ->
                paymentService.renewPaymentSession(FIRST_PAYMENT_ID, FIRST_USER));
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, PAYMENT_STRING,
                FIRST_PAYMENT_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findById(FIRST_PAYMENT_ID);
    }
}
