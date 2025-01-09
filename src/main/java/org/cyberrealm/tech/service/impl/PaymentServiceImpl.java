package org.cyberrealm.tech.service.impl;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.payment.CreatePaymentRequestDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.cyberrealm.tech.dto.stripe.DescriptionForStripeDto;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.exception.PaymentProcessingException;
import org.cyberrealm.tech.mapper.BookingMapper;
import org.cyberrealm.tech.mapper.PaymentMapper;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.service.NotificationService;
import org.cyberrealm.tech.service.PaymentService;
import org.springframework.scheduling.annotation.Async;
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
                new DescriptionForStripeDto(requestDto.bookingId(), total, getDescription(booking))
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
        sendNotification("Payment with id:" + payment.getId() + "\n"
                + "For booking: " + getDescription(payment.getBooking()) + " successfully paid.");
        return paymentMapper.toDtoWithoutSession(payment);
    }

    @Override
    public BookingDto handleCancelledPayment(String sessionId) {
        Payment payment = getPayment(sessionId);
        sendNotification("Payment with id:" + payment.getId() + " can be made later. "
                + "The session is available for only 24 hours.");
        return bookingMapper.toDto(payment.getBooking());
    }

    @Override
    public PaymentDto renewPaymentSession(Long paymentId, User currentUser) {
        Payment payment = getPaymentByIdAndRole(paymentId, currentUser);
        Session newSession = stripeService.renewSession(payment);
        payment.setSessionUrl(getSessionUrl(newSession));
        payment.setSessionId(newSession.getId());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Async
    public void sendNotification(String message) {
        if (notificationService != null) {
            notificationService.sendNotification(message);
        }
    }

    private Payment getPaymentByIdAndRole(Long paymentId, User currentUser) {
        return isManager(currentUser)
                ? paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentProcessingException("Can't find payment by id:"
                        + paymentId))
                : paymentRepository.findByIdAndUserId(paymentId, currentUser.getId())
                .orElseThrow(() -> new PaymentProcessingException("Can't find payment by id:"
                        + paymentId));
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
                new EntityNotFoundException("Can't find payment by session id:" + sessionId)
        );
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
                new EntityNotFoundException("Can't find booking by id: " + id)
        );
    }

    private String getDescription(Booking booking) {
        return "from " + booking.getCheckInDate()
                + " to " + booking.getCheckOutDate()
                + " type " + booking.getAccommodation().getType();
    }
}
