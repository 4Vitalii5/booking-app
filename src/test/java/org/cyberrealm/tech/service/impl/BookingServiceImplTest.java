package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.ACCOMMODATION_STRING;
import static org.cyberrealm.tech.util.TestConstants.BOOKING;
import static org.cyberrealm.tech.util.TestConstants.BOOKING_PROCESSING_EXCEPTION_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.CHECK_IN_DATE;
import static org.cyberrealm.tech.util.TestConstants.CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.DAILY_RATE_23;
import static org.cyberrealm.tech.util.TestConstants.ELECTRICITY;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestConstants.FIRST_AVAILABILITY;
import static org.cyberrealm.tech.util.TestConstants.FIRST_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.HAS_EXPIRED_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.NEW_BOOKING_NOTIFICATION;
import static org.cyberrealm.tech.util.TestConstants.POOL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.STUDIO;
import static org.cyberrealm.tech.util.TestConstants.USER_WITH_PENDING_PAYMENTS_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.WIFI;
import static org.cyberrealm.tech.util.TestUtil.AMENITIES;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_PAGE;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_SEARCH_PARAMETERS;
import static org.cyberrealm.tech.util.TestUtil.CANCELLED_BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_BOOKING_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.CUSTOMER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ACCOMMODATION;
import static org.cyberrealm.tech.util.TestUtil.FIRST_ADDRESS;
import static org.cyberrealm.tech.util.TestUtil.FIRST_BOOKING;
import static org.cyberrealm.tech.util.TestUtil.FIRST_USER;
import static org.cyberrealm.tech.util.TestUtil.INVALID_CREATE_BOOKING_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.MANAGER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.PAGEABLE;
import static org.cyberrealm.tech.util.TestUtil.SECOND_BOOKING;
import static org.cyberrealm.tech.util.TestUtil.SECOND_USER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.exception.BookingForbiddenException;
import org.cyberrealm.tech.exception.BookingProcessingException;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.mapper.BookingMapper;
import org.cyberrealm.tech.mapper.impl.BookingMapperImpl;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.repository.AccommodationRepository;
import org.cyberrealm.tech.repository.PaymentRepository;
import org.cyberrealm.tech.repository.booking.BookingRepository;
import org.cyberrealm.tech.repository.booking.BookingSpecificationBuilder;
import org.cyberrealm.tech.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void beforeEach() {
        MANAGER_ROLE.setRole(Role.RoleName.ROLE_MANAGER);
        CUSTOMER_ROLE.setRole(Role.RoleName.ROLE_CUSTOMER);

        FIRST_USER.setId(FIRST_USER_ID);
        FIRST_USER.setFirstName(FIRST_USER_FIRST_NAME);
        FIRST_USER.setLastName(FIRST_USER_LAST_NAME);
        FIRST_USER.setEmail(FIRST_USER_EMAIL);
        FIRST_USER.setPassword(FIRST_USER_PASSWORD);
        FIRST_USER.setRoles(Set.of(MANAGER_ROLE));

        SECOND_USER.setId(SECOND_USER_ID);
        SECOND_USER.setFirstName(SECOND_USER_FIRST_NAME);
        SECOND_USER.setLastName(SECOND_USER_LAST_NAME);
        SECOND_USER.setEmail(SECOND_USER_EMAIL);
        SECOND_USER.setPassword(SECOND_USER_PASSWORD);
        SECOND_USER.setRoles(Set.of(CUSTOMER_ROLE));

        AMENITIES.add(POOL);
        AMENITIES.add(ELECTRICITY);
        AMENITIES.add(WIFI);

        FIRST_ADDRESS.setId(FIRST_ADDRESS_ID);
        FIRST_ADDRESS.setCountry(FIRST_ADDRESS_COUNTRY);
        FIRST_ADDRESS.setCity(FIRST_ADDRESS_CITY);
        FIRST_ADDRESS.setState(FIRST_ADDRESS_STATE);
        FIRST_ADDRESS.setStreet(FIRST_ADDRESS_STREET);
        FIRST_ADDRESS.setHouseNumber(FIRST_ADDRESS_HOUSE_NUMBER);
        FIRST_ADDRESS.setPostalCode(FIRST_ADDRESS_POSTAL_CODE);

        FIRST_ACCOMMODATION.setId(FIRST_ACCOMMODATION_ID);
        FIRST_ACCOMMODATION.setType(Accommodation.Type.HOUSE);
        FIRST_ACCOMMODATION.setAddress(FIRST_ADDRESS);
        FIRST_ACCOMMODATION.setSize(STUDIO);
        FIRST_ACCOMMODATION.setAmenities(AMENITIES);
        FIRST_ACCOMMODATION.setDailyRate(DAILY_RATE_23);
        FIRST_ACCOMMODATION.setAvailability(FIRST_AVAILABILITY);

        FIRST_BOOKING.setId(FIRST_BOOKING_ID);
        FIRST_BOOKING.setCheckInDate(CHECK_IN_DATE);
        FIRST_BOOKING.setCheckOutDate(CHECK_OUT_DATE);
        FIRST_BOOKING.setAccommodation(FIRST_ACCOMMODATION);
        FIRST_BOOKING.setUser(FIRST_USER);
        FIRST_BOOKING.setStatus(Booking.BookingStatus.PENDING);

        SECOND_BOOKING.setId(SECOND_BOOKING_ID);
        SECOND_BOOKING.setCheckInDate(CHECK_IN_DATE);
        SECOND_BOOKING.setCheckOutDate(SECOND_CHECK_OUT_DATE);
        SECOND_BOOKING.setAccommodation(FIRST_ACCOMMODATION);
        SECOND_BOOKING.setUser(FIRST_USER);
        SECOND_BOOKING.setStatus(Booking.BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Save a valid booking")
    void save_validCreateBookingRequestDto_returnsBookingDto() {
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(FIRST_ACCOMMODATION));
        when(bookingMapper.toEntity(CREATE_BOOKING_REQUEST_DTO)).thenReturn(FIRST_BOOKING);
        when(bookingRepository.save(FIRST_BOOKING)).thenReturn(FIRST_BOOKING);

        BookingDto actual = bookingService.save(CREATE_BOOKING_REQUEST_DTO, FIRST_USER);

        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(accommodationRepository, times(1)).findById(FIRST_ACCOMMODATION_ID);
        verify(bookingMapper, times(2)).toEntity(CREATE_BOOKING_REQUEST_DTO);
        verify(bookingRepository, times(1)).save(FIRST_BOOKING);
        verify(bookingMapper, times(1)).toDto(FIRST_BOOKING);
        verify(notificationService, times(1))
                .sendNotification(String.format(NEW_BOOKING_NOTIFICATION, FIRST_BOOKING.getId()));
    }

    @Test
    @DisplayName("Throw BookingForbiddenException when user has pending payments")
    void save_userWithPendingPayments_throwsBookingForbiddenException() {
        when(paymentRepository.existsPendingPaymentsByUserId(FIRST_USER_ID)).thenReturn(true);
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(FIRST_ACCOMMODATION));

        BookingForbiddenException exception = assertThrows(BookingForbiddenException.class, () ->
                bookingService.save(CREATE_BOOKING_REQUEST_DTO, FIRST_USER));
        String actual = exception.getMessage();
        String expected = String.format(USER_WITH_PENDING_PAYMENTS_EXCEPTION, FIRST_USER_ID);
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).existsPendingPaymentsByUserId(FIRST_USER_ID);
        verify(notificationService, times(0)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Find all bookings")
    void findAll_validPageable_returnsAllBookings() {
        Specification<Booking> specification = bookingSpecificationBuilder
                .build(BOOKING_SEARCH_PARAMETERS);
        when(bookingRepository.findAll(specification, PAGEABLE)).thenReturn(BOOKING_PAGE);

        List<BookingDto> actual = bookingService.search(BOOKING_SEARCH_PARAMETERS, PAGEABLE);

        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(BOOKING_RESPONSE_DTO);
        verify(bookingRepository, times(1)).findAll(specification, PAGEABLE);
    }

    @Test
    @DisplayName("Find booking by ID")
    void findBookingById_validId_returnsBookingDto() {
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_BOOKING));

        BookingDto actual = bookingService.findBookingById(FIRST_BOOKING_ID, FIRST_USER);

        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(bookingMapper, times(1)).toDto(FIRST_BOOKING);
    }

    @Test
    @DisplayName("Find booking by invalid id should throw EntityNotFoundException")
    void findBookingById_invalidId_throwsEntityNotFoundException() {
        when(bookingRepository.findById(INVALID_BOOKING_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.findBookingById(INVALID_BOOKING_ID, FIRST_USER));
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, BOOKING, INVALID_BOOKING_ID);
        assertThat(actual).isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(INVALID_BOOKING_ID);
    }

    @Test
    @DisplayName("Update booking by ID")
    void updateById_validIdAndRequestDto_returnsUpdatedBookingDto() {

        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_BOOKING));
        when(accommodationRepository.existsById(FIRST_ACCOMMODATION_ID))
                .thenReturn(true);

        BookingDto actual = bookingService.updateById(FIRST_USER, FIRST_BOOKING_ID,
                CREATE_BOOKING_REQUEST_DTO);

        BookingDto expected = BOOKING_RESPONSE_DTO;
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
        verify(accommodationRepository, times(1)).existsById(FIRST_ACCOMMODATION_ID);
        verify(bookingMapper, times(1)).toDto(FIRST_BOOKING);
    }

    @Test
    @DisplayName("Update booking with invalid accommodation ID")
    void updateById_invalidAccommodationId_throwsBookingProcessingException() {
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_BOOKING));
        when(accommodationRepository.existsById(INVALID_ACCOMMODATION_ID))
                .thenReturn(false);

        BookingProcessingException exception = assertThrows(BookingProcessingException.class, () ->
                bookingService.updateById(FIRST_USER, FIRST_BOOKING_ID,
                        INVALID_CREATE_BOOKING_REQUEST_DTO));
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(bookingRepository, times(1)).findById(FIRST_BOOKING_ID);
    }

    @Test
    @DisplayName("Delete booking by ID")
    void deleteById_validId_deletesBooking() {
        when(bookingRepository.findById(SECOND_BOOKING_ID))
                .thenReturn(Optional.of(SECOND_BOOKING));

        BookingDto actual = bookingService.deleteById(FIRST_USER, SECOND_BOOKING_ID);

        assertThat(actual).usingRecursiveComparison().isEqualTo(CANCELLED_BOOKING_RESPONSE_DTO);
        verify(bookingRepository, times(1)).findById(SECOND_BOOKING_ID);
        verify(notificationService, times(2)).sendNotification(anyString());
    }

    @Test
    @DisplayName("Ensure booking access for non-manager user without access should throw "
            + "BookingProcessingException")
    void ensureBookingAccess_nonManagerUser_withoutAccess_throwsBookingProcessingException() {
        when(bookingRepository.findById(FIRST_BOOKING_ID))
                .thenReturn(Optional.of(FIRST_BOOKING));

        assertThrows(BookingProcessingException.class, () ->
                bookingService.findBookingById(FIRST_BOOKING_ID, SECOND_USER));
    }

    @Test
    @DisplayName("Validate accommodation availability should throw BookingProcessingException "
            + "when accommodation is not available")
    void validateAccommodationAvailability_accommodationNotAvailable_throwsException() {
        when(bookingRepository.countOverlappingBookings(FIRST_ACCOMMODATION_ID,
                CREATE_BOOKING_REQUEST_DTO.checkInDate(),
                CREATE_BOOKING_REQUEST_DTO.checkOutDate()))
                .thenReturn(FIRST_ACCOMMODATION.getAvailability());
        when(accommodationRepository.findById(FIRST_ACCOMMODATION_ID))
                .thenReturn(Optional.of(FIRST_ACCOMMODATION));

        BookingProcessingException exception = assertThrows(BookingProcessingException.class, () ->
                bookingService.save(CREATE_BOOKING_REQUEST_DTO, FIRST_USER));
        String actual = exception.getMessage();
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
        when(accommodationRepository.findById(INVALID_ACCOMMODATION_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.save(INVALID_CREATE_BOOKING_REQUEST_DTO, FIRST_USER));
        String actual = exception.getMessage();
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, ACCOMMODATION_STRING,
                INVALID_ACCOMMODATION_ID);
        assertThat(actual).isEqualTo(expected);
        verify(accommodationRepository, times(1))
                .findById(INVALID_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Check expired bookings and send notifications")
    void checkExpiredBookings_expiredBookings_foundAndProcessed() {
        when(bookingRepository.findAllByCheckOutDateBeforeAndStatusNot(LocalDate.now().plusDays(1),
                Booking.BookingStatus.CANCELED))
                .thenReturn(List.of(FIRST_BOOKING));
        when(bookingRepository.save(FIRST_BOOKING)).thenReturn(FIRST_BOOKING);

        bookingService.checkExpiredBookings();

        assertThat(FIRST_BOOKING.getStatus()).isEqualTo(Booking.BookingStatus.EXPIRED);
        verify(bookingRepository, times(1))
                .findAllByCheckOutDateBeforeAndStatusNot(LocalDate.now().plusDays(1),
                        Booking.BookingStatus.CANCELED);
        verify(bookingRepository, times(1)).save(FIRST_BOOKING);
        verify(notificationService, times(1)).sendNotification(
                String.format(HAS_EXPIRED_MESSAGE, FIRST_BOOKING.getId()));
    }
}
