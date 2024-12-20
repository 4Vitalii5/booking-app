package org.cyberrealm.tech.service;

import java.util.List;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.booking.BookingSearchParameters;
import org.cyberrealm.tech.dto.booking.CreateBookingRequestDto;
import org.cyberrealm.tech.model.User;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto save(CreateBookingRequestDto requestDto, User user);

    List<BookingDto> search(BookingSearchParameters searchParameters, Pageable pageable);

    List<BookingDto> findBookingsByUserId(Long userId, Pageable pageable);

    BookingDto findBookingById(Long id, User currentUser);

    BookingDto updateById(User currentUser, Long id, CreateBookingRequestDto requestDto);

    BookingDto deleteById(User currentUser, Long id);
}
