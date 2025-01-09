package org.cyberrealm.tech.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.SECOND_USER_EMAIL;
import static org.cyberrealm.tech.util.TestUtil.USER_INFO_UPDATE_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_ROLE_UPDATE_DTO;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cyberrealm.tech.dto.user.UserResponseDto;
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
@Sql(scripts = {
        "classpath:database/roles/add-roles.sql",
        "classpath:database/addresses/add-addresses.sql",
        "classpath:database/accommodations/add-accommodations.sql",
        "classpath:database/users/add-users.sql",
        "classpath:database/users_roles/add-users_roles.sql"
})
@Sql(scripts = {
        "classpath:database/accommodations/remove-accommodations.sql",
        "classpath:database/users/remove-users.sql",
        "classpath:database/roles/remove-roles.sql",
        "classpath:database/users_roles/remove-users_roles.sql",
        "classpath:database/addresses/remove-addresses.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Update user roles")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    void updateRoles_withValidInput_updatesRoles() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(post("/users/{id}/role", FIRST_USER_ID)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_ROLE_UPDATE_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserResponseDto responseDto = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isEqualTo(FIRST_USER_EMAIL);
    }

    @Test
    @DisplayName("Get profile information")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    void getUserInfo_withValidRequest_returnsUserInfo() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserResponseDto responseDto = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.email()).isEqualTo(SECOND_USER_EMAIL);
    }

    @Test
    @DisplayName("Update user profile")
    @WithUserDetails(value = SECOND_USER_EMAIL)
    void updateUserInfo_withValidInput_updatesUserInfo() throws Exception {
        // When
        MvcResult mvcResult = mockMvc.perform(patch("/users/me")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(USER_INFO_UPDATE_DTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserResponseDto responseDto = objectMapper.readValue(jsonResponse, UserResponseDto.class);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.firstName()).isEqualTo(USER_INFO_UPDATE_DTO.firstName());
        assertThat(responseDto.lastName()).isEqualTo(USER_INFO_UPDATE_DTO.lastName());
    }
}
