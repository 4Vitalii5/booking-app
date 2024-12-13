package mate.academy.service;

import java.util.List;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.BookingSearchParameters;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.User;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto save(CreateBookingRequestDto requestDto, User user);

    List<BookingDto> search(BookingSearchParameters searchParameters, Pageable pageable);

    List<BookingDto> findBookingsByUserId(Long userId, Pageable pageable);

    BookingDto findBookingById(Long id);

    BookingDto updateById(Long id, CreateBookingRequestDto requestDto);

    void deleteById(Long id);
}