package org.cyberrealm.tech.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.AMOUNT_FIELD;
import static org.cyberrealm.tech.util.TestConstants.AMOUNT_TO_PAY;
import static org.cyberrealm.tech.util.TestConstants.EXPIRED_PAYMENT_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_SESSION_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.SESSION_ID;
import static org.cyberrealm.tech.util.TestUtil.BOOKING_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.CREATE_PAYMENT_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAID_PAYMENT_WITHOUT_SESSION_DTO;
import static org.cyberrealm.tech.util.TestUtil.PAYMENT_RESPONSE_DTO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberrealm.tech.dto.booking.BookingDto;
import org.cyberrealm.tech.dto.payment.PaymentDto;
import org.cyberrealm.tech.dto.payment.PaymentWithoutSessionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database/roles/remove-roles.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Get users payment information")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/addresses/add-addresses.sql",
            "classpath:database/accommodations/add-accommodations.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql",
            "classpath:database/bookings/add-bookings.sql",
            "classpath:database/payments/add-payments.sql"
    })
    @Sql(scripts = {
            "classpath:database/payments/remove-payments.sql",
            "classpath:database/bookings/remove-bookings.sql",
            "classpath:database/accommodations/remove-accommodations.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/addresses/remove-addresses.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users_roles/remove-users_roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPaymentInfo_withValidRequest_returnsPaymentInfo() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(get("/payments")
                        .param("userId", String.valueOf(FIRST_USER_ID)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PaymentWithoutSessionDto[] responseDtos = objectMapper.readValue(jsonResponse,
                PaymentWithoutSessionDto[].class);
        assertThat(responseDtos).isNotEmpty();
    }

    @Test
    @DisplayName("Initiates payment sessions")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/addresses/add-addresses.sql",
            "classpath:database/accommodations/add-accommodations.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql",
            "classpath:database/bookings/add-bookings.sql",
    })
    @Sql(scripts = {
            "classpath:database/payments/remove-payments.sql",
            "classpath:database/bookings/remove-bookings.sql",
            "classpath:database/accommodations/remove-accommodations.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/addresses/remove-addresses.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users_roles/remove-users_roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_withValidInput_returnsCreatedPayment() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(post("/payments")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(CREATE_PAYMENT_REQUEST_DTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PaymentDto responseDto = objectMapper.readValue(jsonResponse, PaymentDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).usingRecursiveComparison()
                .ignoringFields("sessionUrl", "amount")
                .isEqualTo(PAYMENT_RESPONSE_DTO);
        assertThat(responseDto.amount()).isEqualByComparingTo(AMOUNT_TO_PAY);
    }

    @Test
    @DisplayName("Handles successful payment")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/addresses/add-addresses.sql",
            "classpath:database/accommodations/add-accommodations.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql",
            "classpath:database/bookings/add-bookings.sql",
            "classpath:database/payments/add-payments.sql"
    })
    @Sql(scripts = {
            "classpath:database/payments/remove-payments.sql",
            "classpath:database/bookings/remove-bookings.sql",
            "classpath:database/accommodations/remove-accommodations.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/addresses/remove-addresses.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users_roles/remove-users_roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getSuccessPayment_withValidRequest_returnsPayment() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(get("/payments/success/")
                        .param("sessionId", SECOND_SESSION_ID))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PaymentWithoutSessionDto responseDto = objectMapper.readValue(jsonResponse,
                PaymentWithoutSessionDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).usingRecursiveComparison().ignoringFields(AMOUNT_FIELD)
                .isEqualTo(PAID_PAYMENT_WITHOUT_SESSION_DTO);
        assertThat(responseDto.amount())
                .isEqualByComparingTo(PAID_PAYMENT_WITHOUT_SESSION_DTO.amount());
    }

    @Test
    @DisplayName("Manages payment cancellation")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/addresses/add-addresses.sql",
            "classpath:database/accommodations/add-accommodations.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql",
            "classpath:database/bookings/add-bookings.sql",
            "classpath:database/payments/add-payments.sql"
    })
    @Sql(scripts = {
            "classpath:database/payments/remove-payments.sql",
            "classpath:database/bookings/remove-bookings.sql",
            "classpath:database/accommodations/remove-accommodations.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/addresses/remove-addresses.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users_roles/remove-users_roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCanceledPayment_withValidRequest_returnsBooking() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(get("/payments/cancel/")
                        .param("sessionId", SESSION_ID))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        BookingDto responseDto = objectMapper.readValue(jsonResponse, BookingDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).usingRecursiveComparison().isEqualTo(BOOKING_RESPONSE_DTO);
    }

    @Test
    @DisplayName("Renew the Payment session")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/addresses/add-addresses.sql",
            "classpath:database/accommodations/add-accommodations.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql",
            "classpath:database/bookings/add-bookings.sql",
            "classpath:database/payments/add-payments.sql"
    })
    @Sql(scripts = {
            "classpath:database/payments/remove-payments.sql",
            "classpath:database/bookings/remove-bookings.sql",
            "classpath:database/accommodations/remove-accommodations.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/addresses/remove-addresses.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users_roles/remove-users_roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void renewPaymentSession_withValidRequest_renewsPayment() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(patch("/payments/renew/")
                        .param("paymentId", String.valueOf(EXPIRED_PAYMENT_ID))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PaymentDto responseDto = objectMapper.readValue(jsonResponse, PaymentDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.id()).isEqualTo(EXPIRED_PAYMENT_ID);
    }
}
