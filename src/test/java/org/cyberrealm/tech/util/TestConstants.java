package org.cyberrealm.tech.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.cyberrealm.tech.model.Payment;
import org.cyberrealm.tech.model.Role;

public class TestConstants {
    //User
    public static final long FIRST_USER_ID = 1L;
    public static final String FIRST_USER_EMAIL = "test@example.com";
    public static final String FIRST_USER_FIRST_NAME = "Mike";
    public static final String FIRST_USER_LAST_NAME = "Johnston";
    public static final String FIRST_USER_PASSWORD = "password123";

    public static final long SECOND_USER_ID = 2L;
    public static final String SECOND_USER_EMAIL = "maks@example.com";
    public static final String SECOND_USER_FIRST_NAME = "Matvii";
    public static final String SECOND_USER_LAST_NAME = "Sych";
    public static final String SECOND_USER_PASSWORD = "password";

    public static final String USER_EMAIL = "sych@example.com";
    public static final String USER_FIRST_NAME = "Oleksandr";
    public static final String USER_LAST_NAME = "Sych";
    public static final String NEW_FIRST_NAME = "John";
    public static final String NEW_LAST_NAME = "Diesel";
    public static final String USER_PASSWORD = "password";

    public static final Long INVALID_USER_ID = 99999L;
    public static final String INVALID_USER_EMAIL = "invalid.com";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String NEW_ROLE = "ROLE_MANAGER";

    //Accommodation
    public static final Long FIRST_ACCOMMODATION_ID = 1L;
    public static final Long INVALID_ACCOMMODATION_ID = 99999L;
    public static final String ACCOMMODATION_TYPE_HOUSE = "HOUSE";
    public static final String CONDO = "CONDO";
    public static final String INVALID_TYPE = "INVALID_TYPE";
    public static final int FIRST_AVAILABILITY = 1;
    public static final BigDecimal DAILY_RATE_23 = BigDecimal.valueOf(23.00);
    public static final String STUDIO = "Studio";
    public static final String ACCOMMODATION_STRING = "accommodation";
    public static final String INVALID_TYPE_VALUE = "Invalid type value";

    //Address
    public static final Long FIRST_ADDRESS_ID = 1L;
    public static final Long SECOND_ADDRESS_ID = 2L;
    public static final Long INVALID_ADDRESS_ID = 99999L;

    public static final String FIRST_ADDRESS_COUNTRY = "USA";
    public static final String FIRST_ADDRESS_CITY = "Chicago";
    public static final String FIRST_ADDRESS_STATE = "Illinois";
    public static final String FIRST_ADDRESS_STREET = "Cicero";
    public static final String FIRST_ADDRESS_HOUSE_NUMBER = "49th Ave";
    public static final String FIRST_ADDRESS_POSTAL_CODE = "60804";

    public static final String INVALID_ADDRESS_COUNTRY = "Canada";
    public static final String INVALID_ADDRESS_CITY = "Toronto";
    public static final String INVALID_ADDRESS_STATE = "Ontario";
    public static final String INVALID_ADDRESS_STREET = "Bloor Street";
    public static final String INVALID_ADDRESS_HOUSE_NUMBER = "123";
    public static final String INVALID_ADDRESS_POSTAL_CODE = "M6H 1M9";

    public static final String SECOND_ADDRESS_COUNTRY = "Ukraine";
    public static final String SECOND_ADDRESS_CITY = "Lviv";
    public static final String SECOND_ADDRESS_STATE = "Lvivskyi";
    public static final String SECOND_ADDRESS_STREET = "Shevchenka";
    public static final String SECOND_ADDRESS_HOUSE_NUMBER = "25";
    public static final String SECOND_ADDRESS_POSTAL_CODE = "80352";

    //Booking
    public static final long FIRST_BOOKING_ID = 1L;
    public static final long SECOND_BOOKING_ID = 2L;
    public static final long INVALID_BOOKING_ID = 99999L;
    public static final String BOOKING = "booking";
    public static final LocalDate CHECK_IN_DATE = LocalDate.now();
    public static final LocalDate CHECK_OUT_DATE = LocalDate.now().plusDays(10);
    public static final LocalDate SECOND_CHECK_OUT_DATE = LocalDate.now().plusDays(11);

    //Exception messages
    public static final String ENTITY_NOT_FOUND_EXCEPTION = "Can't find %s by id:%d";
    public static final String ADDRESS_DUPLICATE_RESOURCE_EXCEPTION = "This address %s,%s,%s,%s,%s"
            + ",%s already exists";
    public static final String BOOKED_ADDRESS_DUPLICATE_RESOURCE_EXCEPTION = "This address %s,%s,"
            + "%s,%s,%s,%s already belong another " + ACCOMMODATION_STRING;
    public static final String BOOKING_PROCESSING_EXCEPTION_MESSAGE = "Accommodation is not "
            + "available for the selected dates.";
    public static final String HAS_EXPIRED_MESSAGE = "Booking with ID:%d has expired.";
    public static final String USER_WITH_PENDING_PAYMENTS_EXCEPTION = "User with id:%d has pending "
            + "payments and cannot create new " + BOOKING + ".";
    public static final String NEW_BOOKING_NOTIFICATION = "New " + BOOKING + " created with ID:%d";
    public static final String DUPLICATE_EMAIL_MESSAGE = "User with email: %s already exists";
    public static final String ROLE_NOT_FOUND = "Role: %s not found";
    public static final String ENTITY_NOT_FOUND_BY_SESSION_ID = "Can't find payment by session "
            + "id:%s";
    public static final String NOT_FOUND_EXPIRED_PAYMENTS = "Cannot renew session for payment with "
            + "id:%d. Which has not expired.";
    public static final String EXPIRED_STRIPE_SESSIONS = "Can't check expired Stripe sessions.";
    public static final String CANNOT_CREATE_SESSION = "Cannot create session";

    // Payment
    public static final long FIRST_PAYMENT_ID = 1L;
    public static final long SECOND_PAYMENT_ID = 2L;
    public static final long EXPIRED_PAYMENT_ID = 3L;
    public static final Long INVALID_PAYMENT_ID = 99999L;
    public static final String SESSION_ID = "sessionId";
    public static final String INVALID_SESSION_ID = "123anotherSessionId123";
    public static final String EXPIRED_SESSION_ID = "anotherSessionId123";
    public static final String SECOND_SESSION_ID = "anotherSessionId";
    public static final String SESSION_URL = "http://example.com/session";
    public static final BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(230.00);
    public static final BigDecimal AMOUNT_TO_PAY = BigDecimal.valueOf(1000.00);
    public static final Payment.PaymentStatus PAYMENT_STATUS = Payment.PaymentStatus.PENDING;
    public static final String PENDING = "PENDING";
    public static final int NUMBER_OF_DAYS = 10;
    public static final String AMOUNT_FIELD = "amount";
    public static final String PAYMENT_STRING = "payment";
    public static final String ID_FIELD = "id";
    public static final String EXPIRED = "expired";
    public static final String USER_STRING = "user";

    //Role
    public static final Role.RoleName FIRST_ROLE_NAME = Role.RoleName.ROLE_CUSTOMER;
    public static final Role.RoleName INVALID_ROLE_NAME = Role.RoleName.ROLE_MANAGER;

    //Amenities
    public static final String POOL = "pool";
    public static final String ELECTRICITY = "electricity";
    public static final String WIFI = "wifi";
}
