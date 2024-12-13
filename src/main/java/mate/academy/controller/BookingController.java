package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.BookingSearchParameters;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.User;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Managing users bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_CUSTOMER')")
    @PostMapping
    @Operation(summary = "Create a new accommodation booking",
            description = "Permits the creation of new accommodation bookings.")
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto requestDto,
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookingService.save(requestDto, user);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    @Operation(summary = "Get bookings by user ID and status",
            description = "Retrieves bookings based on user ID and their status.")
    public List<BookingDto> searchBookings(
            @Valid BookingSearchParameters searchParameters, Pageable pageable
    ) {
        return bookingService.search(searchParameters, pageable);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/my")
    @Operation(summary = "Get user bookings", description = "Retrieves user bookings")
    public List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return bookingService.findBookingsByUserId(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID",
            description = "Provides information about a specific booking.")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.findBookingById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_CUSTOMER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update users booking details",
            description = "Allows users to update their booking details.")
    public BookingDto updateBooking(@PathVariable Long id,
                                    @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.updateById(id, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_CUSTOMER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking by ID",
            description = "Enables the cancellation of bookings.")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteById(id);
    }
}
//Booking Controller: Managing users' bookings
//
//POST: /bookings - Permits the creation of new accommodation bookings.
//GET: /bookings/?user_id=...&status=... -
// Retrieves bookings based on user ID and their status. (Available for managers)
//GET: /bookings/my - Retrieves user bookings
//GET: /bookings/{id} - Provides information about a specific booking.
//PUT/PATCH: /bookings/{id} - Allows users to update their booking details.
//        DELETE: /bookings/{id} - Enables the cancellation of bookings.
