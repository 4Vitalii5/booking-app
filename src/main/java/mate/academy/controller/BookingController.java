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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Managing users bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @PostMapping
    @Operation(summary = "Create a new accommodation booking",
            description = "Permits the creation of new accommodation bookings.")
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto requestDto,
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookingService.save(requestDto, user);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    @Operation(summary = "Get bookings by user ID and status",
            description = "Retrieves bookings based on user ID and their status.")
    public List<BookingDto> searchBookings(
            @Valid BookingSearchParameters searchParameters, Pageable pageable
    ) {
        return bookingService.search(searchParameters, pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    @Operation(summary = "Get user bookings", description = "Retrieves user bookings")
    public List<BookingDto> getUserBookings(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return bookingService.findBookingsByUserId(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID",
            description = "Provides information about a specific booking.")
    public BookingDto getBookingById(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return bookingService.findBookingById(id, user);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update users booking details",
            description = "Allows users to update their booking details.")
    public BookingDto updateBooking(Authentication authentication, @PathVariable Long id,
                                    @RequestBody @Valid CreateBookingRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return bookingService.updateById(user, id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel booking by ID",
            description = "Enables the cancellation of bookings.")
    public BookingDto cancelBooking(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return bookingService.deleteById(user, id);
    }
}
