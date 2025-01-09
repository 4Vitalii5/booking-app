package org.cyberrealm.tech.util;

import static org.cyberrealm.tech.util.TestConstants.ACCOMMODATION_TYPE_HOUSE;
import static org.cyberrealm.tech.util.TestConstants.CHECK_IN_DATE;
import static org.cyberrealm.tech.util.TestConstants.CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.CONDO;
import static org.cyberrealm.tech.util.TestConstants.DAILY_RATE_23;
import static org.cyberrealm.tech.util.TestConstants.ELECTRICITY;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_SESSION_ID;
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
import static org.cyberrealm.tech.util.TestConstants.FIRST_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ACCOMMODATION_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.INVALID_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestConstants.INVALID_TYPE;
import static org.cyberrealm.tech.util.TestConstants.NEW_ROLE;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_AMOUNT;
import static org.cyberrealm.tech.util.TestConstants.PAYMENT_STATUS;
import static org.cyberrealm.tech.util.TestConstants.PENDING;
import static org.cyberrealm.tech.util.TestConstants.POOL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_CITY;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_COUNTRY;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_HOUSE_NUMBER;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_POSTAL_CODE;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_STATE;
import static org.cyberrealm.tech.util.TestConstants.SECOND_ADDRESS_STREET;
import static org.cyberrealm.tech.util.TestConstants.SECOND_BOOKING_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_CHECK_OUT_DATE;
import static org.cyberrealm.tech.util.TestConstants.SECOND_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SESSION_URL;
import static org.cyberrealm.tech.util.TestConstants.STUDIO;
import static org.cyberrealm.tech.util.TestConstants.USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.WIFI;

import com.stripe.exception.ApiException;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.cyberrealm.tech.dto.accommodation.AccommodationDto;
import org.cyberrealm.tech.dto.accommodation.CreateAccommodationRequestDto;
import org.cyberrealm.tech.dto.address.CreateAddressRequestDto;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.booking.BookingSearchParameters;
import org.cyberrealm.tech.dto.booking.CreateBookingRequestDto;
import org.cyberrealm.tech.dto.payment.CreatePaymentRequestDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.cyberrealm.tech.dto.stripe.DescriptionForStripeDto;
import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserLoginRequestDto;
import org.cyberrealm.tech.dto.user.UserRegistrationRequestDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.dto.user.UserRoleUpdateDto;
import org.cyberrealm.tech.model.Accommodation;
import org.cyberrealm.tech.model.Address;
import org.cyberrealm.tech.model.Booking;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public class TestUtil {
    //Addresses
    public static final CreateAddressRequestDto CREATE_ADDRESS_REQUEST_DTO =
            new CreateAddressRequestDto(
                    FIRST_ADDRESS_COUNTRY,
                    FIRST_ADDRESS_CITY,
                    FIRST_ADDRESS_STATE,
                    FIRST_ADDRESS_STREET,
                    FIRST_ADDRESS_HOUSE_NUMBER,
                    FIRST_ADDRESS_POSTAL_CODE
            );

    public static final CreateAddressRequestDto SECOND_CREATE_ADDRESS_REQUEST_DTO =
            new CreateAddressRequestDto(
                    SECOND_ADDRESS_COUNTRY,
                    SECOND_ADDRESS_CITY,
                    SECOND_ADDRESS_STATE,
                    SECOND_ADDRESS_STREET,
                    SECOND_ADDRESS_HOUSE_NUMBER,
                    SECOND_ADDRESS_POSTAL_CODE
            );

    public static final CreateAddressRequestDto UPDATE_ADDRESS_REQUEST_DTO =
            new CreateAddressRequestDto(
                    FIRST_ADDRESS_COUNTRY, FIRST_ADDRESS_CITY, FIRST_ADDRESS_STATE,
                    FIRST_ADDRESS_STREET, FIRST_ADDRESS_HOUSE_NUMBER, FIRST_ADDRESS_POSTAL_CODE
            );

    //Accommodations
    public static final CreateAccommodationRequestDto CREATE_ACCOMMODATION_REQUEST_DTO =
            new CreateAccommodationRequestDto(
                    ACCOMMODATION_TYPE_HOUSE,
                    CREATE_ADDRESS_REQUEST_DTO,
                    STUDIO,
                    getAmenities(),
                    DAILY_RATE_23,
                    FIRST_AVAILABILITY
            );

    public static final CreateAccommodationRequestDto SECOND_CREATE_ACCOMMODATION_REQUEST_DTO =
            new CreateAccommodationRequestDto(
                    ACCOMMODATION_TYPE_HOUSE,
                    SECOND_CREATE_ADDRESS_REQUEST_DTO,
                    STUDIO,
                    getAmenities(),
                    DAILY_RATE_23,
                    FIRST_AVAILABILITY
            );

    public static final AccommodationDto ACCOMMODATION_RESPONSE_DTO =
            new AccommodationDto(
                    FIRST_ACCOMMODATION_ID,
                    ACCOMMODATION_TYPE_HOUSE,
                    FIRST_ADDRESS_ID,
                    STUDIO,
                    getAmenities(),
                    DAILY_RATE_23,
                    FIRST_AVAILABILITY
            );

    public static final CreateAccommodationRequestDto UPDATE_ACCOMMODATION_REQUEST_DTO =
            new CreateAccommodationRequestDto(
                    CONDO, UPDATE_ADDRESS_REQUEST_DTO, STUDIO,
                    getAmenities(), BigDecimal.valueOf(150.00), 1
            );

    public static final CreateAccommodationRequestDto INVALID_CREATE_ACCOMMODATION_REQUEST_DTO =
            new CreateAccommodationRequestDto(
                    INVALID_TYPE, CREATE_ADDRESS_REQUEST_DTO, STUDIO,
                    getAmenities(), BigDecimal.valueOf(100.00), 1);

    //Users
    public static final UserRegistrationRequestDto USER_REGISTRATION_REQUEST_DTO =
            new UserRegistrationRequestDto(
                    USER_EMAIL,
                    USER_PASSWORD,
                    USER_PASSWORD,
                    USER_FIRST_NAME,
                    USER_LAST_NAME
            );
    public static final UserRoleUpdateDto USER_ROLE_UPDATE_DTO = new UserRoleUpdateDto(NEW_ROLE);
    public static final UserLoginRequestDto USER_LOGIN_REQUEST_DTO = new UserLoginRequestDto(
            FIRST_USER_EMAIL,
            USER_PASSWORD
    );

    public static final UserInfoUpdateDto USER_INFO_UPDATE_DTO = new UserInfoUpdateDto(
            "UpdatedFirstName",
            "UpdatedLastName"
    );

    public static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto(
            FIRST_USER_ID,
            FIRST_USER_EMAIL,
            FIRST_USER_FIRST_NAME,
            FIRST_USER_LAST_NAME
    );

    //Pages
    public static final PageRequest PAGEABLE = PageRequest.of(0, 1);
    public static final Page<Booking> BOOKING_PAGE = new PageImpl<>(
            List.of(getFirstBooking()), PAGEABLE, 1
    );
    public static final Page<Accommodation> ACCOMMODATION_PAGE = new PageImpl<>(
            List.of(getFirstAccommodation()), PAGEABLE, 1
    );

    //Booking
    public static final CreateBookingRequestDto CREATE_BOOKING_REQUEST_DTO =
            new CreateBookingRequestDto(CHECK_IN_DATE, CHECK_OUT_DATE, FIRST_ACCOMMODATION_ID);

    public static final CreateBookingRequestDto UPDATE_BOOKING_REQUEST_DTO =
            new CreateBookingRequestDto(CHECK_IN_DATE, CHECK_OUT_DATE, FIRST_ACCOMMODATION_ID);

    public static final CreateBookingRequestDto INVALID_CREATE_BOOKING_REQUEST_DTO =
            new CreateBookingRequestDto(CHECK_IN_DATE, CHECK_OUT_DATE, INVALID_ACCOMMODATION_ID);

    public static final BookingDto BOOKING_RESPONSE_DTO = new BookingDto(
            FIRST_BOOKING_ID,
            CHECK_IN_DATE,
            CHECK_OUT_DATE,
            FIRST_ACCOMMODATION_ID,
            Booking.BookingStatus.PENDING
    );

    public static final BookingDto CANCELLED_BOOKING_RESPONSE_DTO = new BookingDto(
            SECOND_BOOKING_ID,
            CHECK_IN_DATE,
            SECOND_CHECK_OUT_DATE,
            FIRST_ACCOMMODATION_ID,
            Booking.BookingStatus.CANCELED
    );

    public static final BookingSearchParameters BOOKING_SEARCH_PARAMETERS =
            new BookingSearchParameters(new String[]{PENDING}, new String[]{"1L"});

    public static final Specification<Booking> BOOKING_SPECIFICATION =
            (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), Booking.BookingStatus.PENDING);

    //Payments
    public static final CreatePaymentRequestDto CREATE_PAYMENT_REQUEST_DTO =
            new CreatePaymentRequestDto(FIRST_BOOKING_ID);

    public static final PaymentDto PAYMENT_RESPONSE_DTO = new PaymentDto(
            FIRST_PAYMENT_ID,
            PAYMENT_AMOUNT,
            SESSION_URL,
            PENDING
    );

    public static final PaymentWithoutSessionDto PAYMENT_WITHOUT_SESSION_DTO =
            new PaymentWithoutSessionDto(
                    FIRST_BOOKING_ID,
                    Payment.PaymentStatus.PENDING,
                    PAYMENT_AMOUNT
            );

    public static final PaymentWithoutSessionDto PAID_PAYMENT_WITHOUT_SESSION_DTO =
            new PaymentWithoutSessionDto(
                    SECOND_BOOKING_ID,
                    Payment.PaymentStatus.PAID,
                    BigDecimal.valueOf(200.00)
            );

    //Stripe
    public static final DescriptionForStripeDto DESCRIPTION_FOR_STRIPE_DTO =
            new DescriptionForStripeDto(getFirstBooking().getId(), PAYMENT_AMOUNT,
                    "Booking #" + getFirstBooking().getId());

    public static final ApiException API_EXCEPTION = new ApiException("Test exception",
            null, null, 0, null);
    public static final Session SESSION = new Session();
    public static final SessionCollection SESSION_COLLECTION = new SessionCollection();

    //Addresses
    public static Address getFirstAddress() {
        Address address = new Address();
        address.setId(FIRST_ADDRESS_ID);
        address.setCountry(FIRST_ADDRESS_COUNTRY);
        address.setCity(FIRST_ADDRESS_CITY);
        address.setState(FIRST_ADDRESS_STATE);
        address.setStreet(FIRST_ADDRESS_STREET);
        address.setHouseNumber(FIRST_ADDRESS_HOUSE_NUMBER);
        address.setPostalCode(FIRST_ADDRESS_POSTAL_CODE);
        return address;
    }

    public static Address getSecondAddress() {
        Address address = new Address();
        address.setId(SECOND_ADDRESS_ID);
        address.setCountry(SECOND_ADDRESS_COUNTRY);
        address.setCity(SECOND_ADDRESS_CITY);
        address.setState(SECOND_ADDRESS_STATE);
        address.setStreet(SECOND_ADDRESS_STREET);
        address.setHouseNumber(SECOND_ADDRESS_HOUSE_NUMBER);
        address.setPostalCode(SECOND_ADDRESS_POSTAL_CODE);
        return address;
    }

    public static Address getInvalidAddress() {
        Address address = new Address();
        address.setId(INVALID_ADDRESS_ID);
        address.setCountry(INVALID_ADDRESS_COUNTRY);
        address.setCity(INVALID_ADDRESS_CITY);
        address.setState(INVALID_ADDRESS_STATE);
        address.setStreet(INVALID_ADDRESS_STREET);
        address.setHouseNumber(INVALID_ADDRESS_HOUSE_NUMBER);
        address.setPostalCode(INVALID_ADDRESS_POSTAL_CODE);
        return address;
    }

    //Accommodations

    public static Accommodation getFirstAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(FIRST_ACCOMMODATION_ID);
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setAddress(getFirstAddress());
        accommodation.setSize(STUDIO);
        accommodation.setAmenities(getAmenities());
        accommodation.setDailyRate(DAILY_RATE_23);
        accommodation.setAvailability(FIRST_AVAILABILITY);
        return accommodation;
    }

    //Roles
    public static Role getCustomerRole() {
        Role role = new Role();
        role.setRole(Role.RoleName.ROLE_CUSTOMER);
        return role;
    }

    public static Role getManagerRole() {
        Role role = new Role();
        role.setRole(Role.RoleName.ROLE_MANAGER);
        return role;
    }

    //Users
    public static User getFirstUser() {
        User user = new User();
        user.setId(FIRST_USER_ID);
        user.setFirstName(FIRST_USER_FIRST_NAME);
        user.setLastName(FIRST_USER_LAST_NAME);
        user.setEmail(FIRST_USER_EMAIL);
        user.setPassword(FIRST_USER_PASSWORD);
        user.setRoles(Set.of(getManagerRole()));
        return user;
    }

    public static User getSecondUser() {
        User user = new User();
        user.setId(SECOND_USER_ID);
        user.setFirstName(SECOND_USER_FIRST_NAME);
        user.setLastName(SECOND_USER_LAST_NAME);
        user.setEmail(SECOND_USER_EMAIL);
        user.setPassword(SECOND_USER_PASSWORD);
        user.setRoles(Set.of(getCustomerRole()));
        return user;
    }

    //Bookings
    public static Booking getFirstBooking() {
        Booking booking = new Booking();
        booking.setId(FIRST_BOOKING_ID);
        booking.setCheckInDate(CHECK_IN_DATE);
        booking.setCheckOutDate(CHECK_OUT_DATE);
        booking.setAccommodation(getFirstAccommodation());
        booking.setUser(getFirstUser());
        booking.setStatus(Booking.BookingStatus.PENDING);
        return booking;
    }

    public static Booking getSecondBooking() {
        Booking booking = new Booking();
        booking.setId(SECOND_BOOKING_ID);
        booking.setCheckInDate(CHECK_IN_DATE);
        booking.setCheckOutDate(SECOND_CHECK_OUT_DATE);
        booking.setAccommodation(getFirstAccommodation());
        booking.setUser(getFirstUser());
        booking.setStatus(Booking.BookingStatus.PENDING);
        return booking;
    }

    //Payments
    public static Payment getFirstPayment() throws MalformedURLException {
        Payment payment = new Payment();
        payment.setId(FIRST_PAYMENT_ID);
        payment.setBooking(getFirstBooking());
        payment.setAmountToPay(PAYMENT_AMOUNT);
        payment.setSessionId(SESSION_ID);
        payment.setSessionUrl(new URL(SESSION_URL));
        payment.setStatus(PAYMENT_STATUS);
        return payment;
    }

    public static Payment getSecondPayment() throws MalformedURLException {
        Payment payment = new Payment();
        payment.setId(SECOND_PAYMENT_ID);
        payment.setBooking(getSecondBooking());
        payment.setAmountToPay(PAYMENT_AMOUNT);
        payment.setSessionId(SECOND_SESSION_ID);
        payment.setSessionUrl(new URL(SESSION_URL));
        payment.setStatus(PAYMENT_STATUS);
        return payment;
    }

    public static Payment getExpiredPayment() throws MalformedURLException {
        Payment payment = new Payment();
        payment.setId(EXPIRED_PAYMENT_ID);
        payment.setBooking(getSecondBooking());
        payment.setAmountToPay(PAYMENT_AMOUNT);
        payment.setSessionId(EXPIRED_SESSION_ID);
        payment.setSessionUrl(new URL(SESSION_URL));
        payment.setStatus(Payment.PaymentStatus.EXPIRED);
        return payment;
    }

    //Amenities
    public static List<String> getAmenities() {
        List<String> amenities = new ArrayList<>();
        amenities.add(POOL);
        amenities.add(ELECTRICITY);
        amenities.add(WIFI);
        return amenities;
    }
}
