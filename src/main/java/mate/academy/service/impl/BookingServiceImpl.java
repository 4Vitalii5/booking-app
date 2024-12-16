package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.BookingSearchParameters;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.BookingProcessingException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.model.Booking;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.booking.BookingRepository;
import mate.academy.repository.booking.BookingSpecificationBuilder;
import mate.academy.service.AccommodationService;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final int MIN_AVAILABILITY = 1;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;
    private final AccommodationService accommodationService;

    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        checkAccommodationDates(requestDto);
        checkAccommodationAvailability(requestDto);
        Booking booking = bookingMapper.toEntity(requestDto);
        booking.setUser(user);
        booking.setStatus(Booking.BookingStatus.PENDING);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> search(BookingSearchParameters searchParameters,
                                   Pageable pageable) {
        Specification<Booking> bookingSpecification = bookingSpecificationBuilder
                .build(searchParameters);
        return bookingRepository.findAll(bookingSpecification, pageable)
                .stream()
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
        ensureBookingAccess(currentUser, booking);
        bookingMapper.updateBookingFromDto(requestDto, booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteById(User currentUser, Long id) {
        Booking booking = getBookingById(id);
        ensureBookingAccess(currentUser, booking);
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new BookingProcessingException("Booking with id:" + id
                    + " already cancelled");
        }
        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
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

    private void checkAccommodationDates(CreateBookingRequestDto requestDto) {
        if (bookingRepository.existsByCheckInDateAndCheckOutDateAndAccommodationId(
                requestDto.checkInDate(), requestDto.checkOutDate(), requestDto.accommodationId()
        )) {
            throw new BookingProcessingException(
                    "Accommodation with id: " + requestDto.accommodationId()
                            + " already booked for these dates: " + requestDto.checkInDate()
                            + "-" + requestDto.checkOutDate()
            );
        }
    }

    private void checkAccommodationAvailability(CreateBookingRequestDto requestDto) {
        if (accommodationService.findById(requestDto.accommodationId()).availability()
                < MIN_AVAILABILITY) {
            throw new BookingProcessingException("Accommodation with id "
                    + requestDto.accommodationId() + " is not available now.");
        }
    }

    private boolean isNotManager(User currentUser) {
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.getRole().equals(Role.RoleName.ROLE_CUSTOMER));
    }
}
