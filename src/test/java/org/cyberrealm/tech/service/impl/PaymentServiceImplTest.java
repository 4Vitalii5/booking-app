package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.AMOUNT_FIELD;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_BY_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.ID_FIELD;
import static org.cyberrealm.tech.util.TestConstants.NUMBER_OF_DAYS;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_AMOUNT;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_STRING;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SESSION_URL;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_PAYMENT_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAID_PAYMENT_WITHOUT_SESSION_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAYMENT_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAYMENT_WITHOUT_SESSION_DTO;
import static org.cyberrealm.tech.util.TestUtil.getFirstBooking;
import static org.cyberrealm.tech.util.TestUtil.getFirstPayment;
import static org.cyberrealm.tech.util.TestUtil.getFirstUser;
import static org.cyberrealm.tech.util.TestUtil.getSecondPayment;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
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
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.service.NotificationService;
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

    @Test
    @DisplayName("Create payment successfully")
    void createPayment_validRequestDto_returnsPaymentDto() throws MalformedURLException {
        //Given
        Booking booking = getFirstBooking();
        Payment payment = getFirstPayment();
        Session mockSession = mock(Session.class);
        when(bookingRepository.findById(FIRST_BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.numberOfDays(FIRST_BOOKING_ID)).thenReturn(NUMBER_OF_DAYS);
        when(mockSession.getUrl()).thenReturn(SESSION_URL);
        when(mockSession.getId()).thenReturn(SESSION_ID);
        when(stripeService.createStripeSession(any(DescriptionForStripeDto.class)))
                .thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        //When
        PaymentDto actual = paymentService.createPayment(CREATE_PAYMENT_REQUEST_DTO);

        //Then
        PaymentDto expected = PAYMENT_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().ignoringFields(ID_FIELD).isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(stripeService, times(1)).createStripeSession(any(DescriptionForStripeDto.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Handle successful payment")
    void handleSuccessPayment_validSessionId_returnsPaymentWithoutSessionDto()
            throws MalformedURLException {
        //Given
        Payment payment = getSecondPayment();
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));

        //When
        PaymentWithoutSessionDto actual = paymentService.handleSuccessPayment(SESSION_ID);

        //Then
        PaymentWithoutSessionDto expected = PAID_PAYMENT_WITHOUT_SESSION_DTO;
        assertThat(actual).usingRecursiveComparison().ignoringFields(AMOUNT_FIELD)
                .isEqualTo(expected);
        assertThat(actual.amount()).isEqualTo(PAYMENT_AMOUNT);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(notificationService, times(1)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Handle cancelled payment")
    void handleCancelledPayment_validSessionId_returnsBookingDto() throws MalformedURLException {
        //Given
        Payment payment = getFirstPayment();
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));

        //When
        BookingDto actual = paymentService.handleCancelledPayment(SESSION_ID);

        //Then
        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
        verify(notificationService, times(1)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Renew payment session")
    void renewPaymentSession_validPaymentId_returnsUpdatedPaymentDto()
            throws MalformedURLException {
        //Given
        Payment payment = getFirstPayment();
        User user = getFirstUser();
        Session mockSession = mock(Session.class);
        when(paymentRepository.findById(FIRST_PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(mockSession.getUrl()).thenReturn(SESSION_URL);
        when(mockSession.getId()).thenReturn(SESSION_ID);
        when(stripeService.renewSession(payment)).thenReturn(mockSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        //When
        PaymentDto actual = paymentService.renewPaymentSession(FIRST_PAYMENT_ID, user);

        //Then
        PaymentDto expected = PAYMENT_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(paymentRepository, times(1)).findById(FIRST_PAYMENT_ID);
        verify(stripeService, times(1)).renewSession(payment);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Get payment info by user ID")
    void getPaymentInfo_validUserId_returnsPaymentWithoutSessionDtoList()
            throws MalformedURLException {
        //Given
        Payment payment = getFirstPayment();
        Booking booking = getFirstBooking();
        User user = getFirstUser();
        when(paymentRepository.findByBookingId(FIRST_BOOKING_ID)).thenReturn(Optional.of(payment));
        when(bookingRepository.findAllByUserId(FIRST_USER_ID)).thenReturn(List.of(booking));

        //When
        List<PaymentWithoutSessionDto> actual = paymentService.getPaymentInfo(FIRST_USER_ID,
                user);

        //Then
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(PAYMENT_WITHOUT_SESSION_DTO);
        verify(paymentRepository, times(1)).findByBookingId(FIRST_BOOKING_ID);
        verify(bookingRepository, times(1)).findAllByUserId(FIRST_USER_ID);
    }

    @Test
    @DisplayName("Get payment by session ID throws EntityNotFoundException")
    void getPayment_invalidSessionId_throwsEntityNotFoundException() {
        //Given
        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                paymentService.handleSuccessPayment(SESSION_ID));

        //Then
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_BY_SESSION_ID, SESSION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findBySessionId(SESSION_ID);
    }

    @Test
    @DisplayName("Renew payment session with invalid payment ID throws PaymentProcessingException")
    void renewPaymentSession_invalidPaymentId_throwsPaymentProcessingException() {
        //Given
        User user = getFirstUser();
        when(paymentRepository.findById(FIRST_PAYMENT_ID)).thenReturn(Optional.empty());

        //When
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () ->
                paymentService.renewPaymentSession(FIRST_PAYMENT_ID, user));

        //Then
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, PAYMENT_STRING,
                FIRST_PAYMENT_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findById(FIRST_PAYMENT_ID);
    }
}
