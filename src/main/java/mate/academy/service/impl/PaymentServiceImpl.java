package mate.academy.service.impl;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.payment.CreatePaymentRequestDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.dto.payment.PaymentWithoutSessionDto;
import mate.academy.dto.stripe.DescriptionForStripeDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import mate.academy.service.NotificationService;
import mate.academy.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public List<PaymentWithoutSessionDto> getPaymentInfo(Long userId, User currentUser) {
        return getBookingIdsByRole(userId, currentUser).stream()
                .map(paymentRepository::findByBookingId)
                .flatMap(Optional::stream)
                .map(paymentMapper::toDtoWithoutSession)
                .toList();
    }

    @Transactional
    @Override
    public PaymentDto createPayment(CreatePaymentRequestDto requestDto) {
        Booking booking = getBookingById(requestDto.bookingId());
        BigDecimal total = calculateTotalPrice(booking);
        Payment payment = paymentMapper.toEntity(requestDto);
        Session session = stripeService.createStripeSession(
                new DescriptionForStripeDto(
                        requestDto.bookingId(),
                        total,
                        getDescription(booking)
                )
        );
        payment.setAmountToPay(total);
        payment.setSessionUrl(getSessionUrl(session));
        payment.setSessionId(session.getId());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Transactional
    @Override
    public PaymentWithoutSessionDto handleSuccessPayment(String sessionId) {
        Payment payment = getPayment(sessionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.getBooking().setStatus(Booking.BookingStatus.CONFIRMED);
        paymentRepository.save(payment);
        return paymentMapper.toDtoWithoutSession(payment);
    }

    @Override
    public BookingDto handleCancelledPayment(String sessionId) {
        Payment payment = getPayment(sessionId);
        notificationService.sendNotification("Payment #:" + payment.getId() + " can be made later. "
                + "The session is available for only 24 hours.");
        return bookingMapper.toDto(payment.getBooking());
    }

    private URL getSessionUrl(Session session) {
        try {
            return new URL(session.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL format", e);
        }
    }

    private Payment getPayment(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Can't find payment with session id:" + sessionId));
    }

    private List<Long> getBookingIdsByRole(Long userId, User currentUser) {
        return isManager(currentUser)
                ? getBookingIdsByUserId(userId) : getBookingIdsByUserId(currentUser.getId());
    }

    private List<Long> getBookingIdsByUserId(Long userId) {
        return bookingRepository.findAllByUserId(userId).stream()
                .map(Booking::getId)
                .toList();
    }

    private boolean isManager(User currentUser) {
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(Role.RoleName.ROLE_MANAGER));
    }

    private BigDecimal calculateTotalPrice(Booking booking) {
        int numberOfDays = bookingRepository.numberOfDays(booking.getId());
        BigDecimal dailyRate = booking.getAccommodation().getDailyRate();
        return dailyRate.multiply(BigDecimal.valueOf(numberOfDays));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find booking with id: " + id)
        );
    }

    private String getDescription(Booking booking) {
        return "Booking #" + booking.getId()
                + " from " + booking.getCheckInDate()
                + " to " + booking.getCheckOutDate()
                + " type " + booking.getAccommodation().getType();
    }
}
