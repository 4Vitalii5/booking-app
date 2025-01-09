package org.cyberrealm.tech.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.DUPLICATE_EMAIL_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestUtil.USER_LOGIN_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_REGISTRATION_REQUEST_DTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberrealm.tech.dto.user.UserLoginResponseDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database/roles/remove-roles.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Register a new user")
    @WithMockUser(username = FIRST_USER_EMAIL, roles = "CUSTOMER")
    @Sql(scripts = "classpath:database/roles/add-roles.sql")
    @Sql(scripts = {
            "classpath:database/users_roles/remove-users_roles.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users/remove-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_withValidInput_createsNewUser() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_REGISTRATION_REQUEST_DTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserResponseDto responseDto = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isEqualTo(USER_REGISTRATION_REQUEST_DTO.email());
    }

    @Test
    @DisplayName("Authenticate user")
    @WithMockUser(username = FIRST_USER_EMAIL, roles = "CUSTOMER")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users_roles/add-users_roles.sql"
    })
    @Sql(scripts = {
            "classpath:database/users_roles/remove-users_roles.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users/remove-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void login_withValidInput_returnsToken() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_LOGIN_REQUEST_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);
        UserLoginResponseDto responseDto = objectMapper.readValue(jsonResponse,
                UserLoginResponseDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.token()).isNotEmpty();
    }

    @Test
    @DisplayName("Register user with existing email")
    @Sql(scripts = "classpath:database/roles/add-roles.sql")
    @Sql(scripts = {
            "classpath:database/users_roles/remove-users_roles.sql",
            "classpath:database/roles/remove-roles.sql",
            "classpath:database/users/remove-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_withExistingEmail_throwsRegistrationException() throws Exception {
        // When
        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_REGISTRATION_REQUEST_DTO)))
                .andExpect(status().isCreated());

        MvcResult mvcResult = mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_REGISTRATION_REQUEST_DTO)))
                .andExpect(status().isConflict())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertThat(jsonResponse).contains(
                String.format(DUPLICATE_EMAIL_MESSAGE, USER_REGISTRATION_REQUEST_DTO.email())
        );
    }
}
