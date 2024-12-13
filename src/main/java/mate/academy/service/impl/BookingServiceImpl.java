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
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import mate.academy.repository.booking.BookingRepository;
import mate.academy.repository.booking.BookingSpecificationBuilder;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final BookingSpecificationBuilder bookingSpecificationBuilder;

    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        if (bookingRepository.existsByCheckInDateAndCheckOutDateAndAccommodationId(
                requestDto.checkInDate(), requestDto.checkOutDate(), requestDto.accommodationId()
        )) {
            throw new BookingProcessingException(
                    "Accommodation with id: " + requestDto.accommodationId()
                            + " already booked for these dates: " + requestDto.checkInDate()
                            + "-" + requestDto.checkOutDate()
            );
        }
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
    public BookingDto findBookingById(Long id) {
        Booking booking = getBookingById(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateById(Long id, CreateBookingRequestDto requestDto) {
        Booking booking = getBookingById(id);
        bookingMapper.updateBookingFromDto(requestDto, booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find booking with id: " + id)
        );
    }
}
