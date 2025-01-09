package org.cyberrealm.tech.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.booking.BookingSearchParameters;
import org.cyberrealm.tech.dto.booking.CreateBookingRequestDto;
import org.cyberrealm.tech.exception.BookingForbiddenException;
import org.cyberrealm.tech.exception.BookingProcessingException;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.mapper.BookingMapper;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.AccommodationRepository;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.repository.booking.BookingSpecificationBuilder;
import org.cyberrealm.tech.service.BookingService;
import org.cyberrealm.tech.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final int DAYS_TO_ADD = 1;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;
    private final AccommodationRepository accommodationRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        validateAccommodationAvailability(requestDto);
        if (paymentRepository.existsPendingPaymentsByUserId(user.getId())) {
            throw new BookingForbiddenException("User with id:" + user.getId()
                    + " has pending payments and cannot create new booking.");
        }
        Booking booking = bookingMapper.toEntity(requestDto);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        bookingRepository.save(booking);
        sendNotification("New booking created with ID:" + booking.getId());
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> search(BookingSearchParameters searchParameters, Pageable pageable) {
        Specification<Booking> bookingSpecification = bookingSpecificationBuilder
                .build(searchParameters);
        return bookingRepository.findAll(bookingSpecification, pageable).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> findBookingsByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingDto findBookingById(Long id, User currentUser) {
        Booking booking = getBookingById(id);
        ensureBookingAccess(currentUser, booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateById(User currentUser, Long id, CreateBookingRequestDto requestDto) {
        Booking booking = getBookingById(id);
        if (!accommodationRepository.existsById(requestDto.accommodationId())) {
            throw new BookingProcessingException("Can't find accommodation by id:"
                    + requestDto.accommodationId());
        }
        ensureBookingAccess(currentUser, booking);
        bookingMapper.updateBookingFromDto(requestDto, booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto deleteById(User currentUser, Long id) {
        Booking booking = getBookingById(id);
        ensureBookingAccess(currentUser, booking);
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new BookingProcessingException("Booking with id:" + id + " already cancelled");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
        sendNotification("Booking with ID:" + booking.getId() + " has been cancelled");
        sendNotification("Accommodation with ID:" + booking.getAccommodation().getId()
                + " has released.");
        return bookingMapper.toDto(booking);
    }

    @Scheduled(cron = "${cron.expression}")
    public void checkExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findAllByCheckOutDateBeforeAndStatusNot(
                LocalDate.now().plusDays(DAYS_TO_ADD), Booking.BookingStatus.CANCELED
        );
        if (expiredBookings.isEmpty()) {
            sendNotification("No expired bookings today!");
        } else {
            for (Booking booking : expiredBookings) {
                booking.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(booking);
                sendNotification("Booking with ID:" + booking.getId() + " has expired.");
            }
        }
    }

    @Async
    public void sendNotification(String message) {
        if (notificationService != null) {
            notificationService.sendNotification(message);
        }
    }

    private void ensureBookingAccess(User currentUser, Booking booking) {
        if (isNotManager(currentUser) && !booking.getUser().equals(currentUser)) {
            throw new BookingProcessingException("Can't find booking by id:" + booking.getId());
        }
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find booking by id:" + id)
        );
    }

    private boolean isNotManager(User currentUser) {
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(Role.RoleName.ROLE_CUSTOMER));
    }

    private void validateAccommodationAvailability(CreateBookingRequestDto requestDto) {
        int overlappingBookings = bookingRepository.countOverlappingBookings(
                requestDto.accommodationId(),
                requestDto.checkInDate(),
                requestDto.checkOutDate()
        );

        Accommodation accommodation = accommodationRepository.findById(requestDto.accommodationId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find accommodation by id:"
                        + requestDto.accommodationId()));

        if (overlappingBookings >= accommodation.getAvailability()) {
            throw new BookingProcessingException("Accommodation is not available for the selected "
                    + "dates.");
        }
    }
}
