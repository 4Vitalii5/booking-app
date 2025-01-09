package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.ACCOMMODATION_STRING;
import static org.cyberrealm.tech.util.TestConstants.BOOKING;
import static org.cyberrealm.tech.util.TestConstants.BOOKING_PROCESSING_EXCEPTION_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.HAS_EXPIRED_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.NEW_BOOKING_NOTIFICATION;
import static org.cyberrealm.tech.util.TestConstants.SECOND_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.USER_WITH_PENDING_PAYMENTS_EXCEPTION;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_PAGE;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_SEARCH_PARAMETERS;
import static org.cyberrealm.tech.util.TestUtil.CANCELLED_BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_BOOKING_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.INVALID_CREATE_BOOKING_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAGEABLE;
import static org.cyberrealm.tech.util.TestUtil.getFirstAccommodation;
import static org.cyberrealm.tech.util.TestUtil.getFirstBooking;
import static org.cyberrealm.tech.util.TestUtil.getFirstUser;
import static org.cyberrealm.tech.util.TestUtil.getSecondBooking;
import static org.cyberrealm.tech.util.TestUtil.getSecondUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.exception.BookingForbiddenException;
import org.cyberrealm.tech.exception.BookingProcessingException;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.mapper.BookingMapper;
import org.cyberrealm.tech.mapper.impl.BookingMapperImpl;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.AccommodationRepository;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.repository.booking.BookingSpecificationBuilder;
import org.cyberrealm.tech.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();
    @Mock
    private BookingSpecificationBuilder bookingSpecificationBuilder;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Save a valid booking")
    void save_validCreateBookingRequestDto_returnsBookingDto() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        Booking booking = getFirstBooking();
        User user = getFirstUser();
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));
        when(bookingMapper.toEntity(CREATE_BOOKING_REQUEST_DTO)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);

        //When
        BookingDto actual = bookingService.save(CREATE_BOOKING_REQUEST_DTO, user);

        //Then
        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(accommodationRepository, times(1)).findById(FIRST_ACCOMMODATION_ID);
        verify(bookingMapper, times(2)).toEntity(CREATE_BOOKING_REQUEST_DTO);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapper, times(1)).toDto(booking);
        verify(notificationService, times(1))
                .sendNotification(String.format(NEW_BOOKING_NOTIFICATION, booking.getId()));
    }

    @Test
    @DisplayName("Throw BookingForbiddenException when user has pending payments")
    void save_userWithPendingPayments_throwsBookingForbiddenException() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        User user = getFirstUser();
        when(paymentRepository.existsPendingPaymentsByUserId(FIRST_USER_ID)).thenReturn(true);
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));

        //When
        BookingForbiddenException exception = assertThrows(BookingForbiddenException.class, () ->
                bookingService.save(CREATE_BOOKING_REQUEST_DTO, user));

        //Then
        String actual = exception.getMessage();
        String expected = String.format(USER_WITH_PENDING_PAYMENTS_EXCEPTION, FIRST_USER_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).existsPendingPaymentsByUserId(FIRST_USER_ID);
        verify(notificationService, times(0)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Find all bookings")
    void findAll_validPageable_returnsAllBookings() {
        //Given
        Specification<Booking> specification = bookingSpecificationBuilder
                .build(BOOKING_SEARCH_PARAMETERS);
        when(bookingRepository.findAll(specification, PAGEABLE)).thenReturn(BOOKING_PAGE);

        //When
        List<BookingDto> actual = bookingService.search(BOOKING_SEARCH_PARAMETERS, PAGEABLE);

        //Then
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(BOOKING_RESPONSE_DTO);
        verify(bookingRepository, times(1)).findAll(specification, PAGEABLE);
    }

    @Test
    @DisplayName("Find booking by ID")
    void findBookingById_validId_returnsBookingDto() {
        //Given
        Booking booking = getFirstBooking();
        User user = getFirstUser();
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(booking));

        //When
        BookingDto actual = bookingService.findBookingById(FIRST_BOOKING_ID, user);

        //Then
        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    @DisplayName("Find booking by invalid id should throw EntityNotFoundException")
    void findBookingById_invalidId_throwsEntityNotFoundException() {
        //Given
        User user = getFirstUser();
        when(bookingRepository.findById(INVALID_BOOKING_ID))
                .thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.findBookingById(INVALID_BOOKING_ID, user));

        //Then
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, BOOKING, INVALID_BOOKING_ID);
        assertThat(actual).isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(INVALID_BOOKING_ID);
    }

    @Test
    @DisplayName("Update booking by ID")
    void updateById_validIdAndRequestDto_returnsUpdatedBookingDto() {
        //Given
        Booking booking = getFirstBooking();
        User user = getFirstUser();
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(booking));
        when(accommodationRepository.existsById(FIRST_ACCOMMODATION_ID))
                .thenReturn(true);

        //When
        BookingDto actual = bookingService.updateById(user, FIRST_BOOKING_ID,
                CREATE_BOOKING_REQUEST_DTO);

        //Then
        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(accommodationRepository, times(1)).existsById(FIRST_ACCOMMODATION_ID);
        verify(bookingMapper, times(1)).toDto(booking);
    }

    @Test
    @DisplayName("Update booking with invalid accommodation ID")
    void updateById_invalidAccommodationId_throwsBookingProcessingException() {
        //Given
        Booking booking = getFirstBooking();
        User user = getFirstUser();
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(booking));
        when(accommodationRepository.existsById(INVALID_ACCOMMODATION_ID))
                .thenReturn(false);

        //When
        BookingProcessingException exception = assertThrows(BookingProcessingException.class, () ->
                bookingService.updateById(user, FIRST_BOOKING_ID,
                        INVALID_CREATE_BOOKING_REQUEST_DTO));

        //Then
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
    }

    @Test
    @DisplayName("Delete booking by ID")
    void deleteById_validId_deletesBooking() {
        //Given
        Booking booking = getSecondBooking();
        User user = getFirstUser();
        when(bookingRepository.findById(SECOND_BOOKING_ID))
                .thenReturn(Optional.of(booking));

        //When
        BookingDto actual = bookingService.deleteById(user, SECOND_BOOKING_ID);

        //Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(CANCELLED_BOOKING_RESPONSE_DTO);
        verify(bookingRepository, times(1)).findById(SECOND_BOOKING_ID);
        verify(notificationService, times(2)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Ensure booking access for non-manager user without access should throw "
            + "BookingProcessingException")
    void ensureBookingAccess_nonManagerUser_withoutAccess_throwsBookingProcessingException() {
        //Given
        Booking booking = getFirstBooking();
        User user = getSecondUser();
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(booking));

        //When
        BookingProcessingException exception = assertThrows(BookingProcessingException.class, () ->
                bookingService.findBookingById(FIRST_BOOKING_ID, user));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, BOOKING, booking.getId());
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    @DisplayName("Validate accommodation availability should throw BookingProcessingException "
            + "when accommodation is not available")
    void validateAccommodationAvailability_accommodationNotAvailable_throwsException() {
        //Given
        Accommodation accommodation = getFirstAccommodation();
        User user = getFirstUser();
        when(bookingRepository.countOverlappingBookings(FIRST_ACCOMMODATION_ID,
                CREATE_BOOKING_REQUEST_DTO.checkInDate(),
                CREATE_BOOKING_REQUEST_DTO.checkOutDate()))
                .thenReturn(accommodation.getAvailability());
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(accommodation));

        //When
        BookingProcessingException exception = assertThrows(BookingProcessingException.class, () ->
                bookingService.save(CREATE_BOOKING_REQUEST_DTO, user));
        String actual = exception.getMessage();

        //Then
        String expected = BOOKING_PROCESSING_EXCEPTION_MESSAGE;
        assertThat(actual).isEqualTo(expected);
        verify(bookingRepository, times(1))
                .countOverlappingBookings(FIRST_ACCOMMODATION_ID,
                        CREATE_BOOKING_REQUEST_DTO.checkInDate(),
                        CREATE_BOOKING_REQUEST_DTO.checkOutDate());
        verify(accommodationRepository, times(1))
                .findById(FIRST_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Validate accommodation availability should throw EntityNotFoundException when "
            + "accommodation not found")
    void validateAccommodationAvailability_accommodationNotFound_throwsEntityNotFoundException() {
        //Given
        User user = getFirstUser();
        when(accommodationRepository.findById(INVALID_ACCOMMODATION_ID))
                .thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.save(INVALID_CREATE_BOOKING_REQUEST_DTO, user));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(accommodationRepository, times(1))
                .findById(INVALID_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Check expired bookings and send notifications")
    void checkExpiredBookings_expiredBookings_foundAndProcessed() {
        //Given
        Booking booking = getFirstBooking();
        when(bookingRepository.findAllByCheckOutDateBeforeAndStatusNot(LocalDate.now().plusDays(1),
                Booking.BookingStatus.CANCELED))
                .thenReturn(List.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        //When
        bookingService.checkExpiredBookings();

        //Then
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.EXPIRED);
        verify(bookingRepository, times(1))
                .findAllByCheckOutDateBeforeAndStatusNot(LocalDate.now().plusDays(1),
                        Booking.BookingStatus.CANCELED);
        verify(bookingRepository, times(1)).save(booking);
        verify(notificationService, times(1)).sendNotification(
                String.format(HAS_EXPIRED_MESSAGE, booking.getId()));
    }
}
