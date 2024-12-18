package mate.academy.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.BookingSearchParameters;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.BookingForbiddenException;
import mate.academy.exception.BookingProcessingException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.model.Accommodation;
import mate.academy.model.Booking;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.AccommodationRepository;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import mate.academy.repository.booking.BookingSpecificationBuilder;
import mate.academy.service.BookingService;
import mate.academy.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
                    + " has pending payments and cannot create "
                    + "new booking.");
        }
        Booking booking = bookingMapper.toEntity(requestDto);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        bookingRepository.save(booking);
        notificationService.sendNotification("New booking created with ID:" + booking.getId());
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
            throw new BookingProcessingException("Can't find accommodation with id:"
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
            throw new BookingProcessingException("Booking with id:" + id
                    + " already cancelled");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
        notificationService.sendNotification("Booking with ID:" + booking.getId()
                + " has been cancelled");
        notificationService.sendNotification("Accommodation with ID:"
                + booking.getAccommodation().getId() + " has released.");
        return bookingMapper.toDto(booking);
    }

    @Scheduled(cron = "${cron.expression}")
    public void checkExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findAllByCheckOutDateBeforeAndStatusNot(
                LocalDate.now().plusDays(DAYS_TO_ADD), Booking.BookingStatus.CANCELED
        );
        if (expiredBookings.isEmpty()) {
            notificationService.sendNotification("No expired bookings today!");
        } else {
            for (Booking booking : expiredBookings) {
                booking.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(booking);
                notificationService.sendNotification("Booking with ID:" + booking.getId()
                        + "has expired.");
            }
        }
    }

    private void ensureBookingAccess(User currentUser, Booking booking) {
        if (isNotManager(currentUser) && !booking.getUser().equals(currentUser)) {
            throw new BookingProcessingException("Can't find booking with id:" + booking.getId());
        }
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find booking with id: " + id)
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
                .orElseThrow(() -> new EntityNotFoundException("Can't find accommodation with id:"
                        + requestDto.accommodationId()));

        if (overlappingBookings >= accommodation.getAvailability()) {
            throw new BookingProcessingException("Accommodation is not available for the selected "
                    + "dates.");
        }
    }
}
