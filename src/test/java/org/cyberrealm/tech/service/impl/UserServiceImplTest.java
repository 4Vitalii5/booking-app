package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.service.impl.UserServiceImpl.DEFAULT_ROLE;
import static org.cyberrealm.tech.util.TestConstants.ENCODED_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_EMAIL;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.NEW_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.NEW_LAST_NAME;
import static org.cyberrealm.tech.util.TestUtil.CUSTOMER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.FIRST_USER;
import static org.cyberrealm.tech.util.TestUtil.MANAGER_ROLE;
import static org.cyberrealm.tech.util.TestUtil.USER_REGISTRATION_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_ROLE_UPDATE_DTO;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.exception.RegistrationException;
import org.cyberrealm.tech.mapper.UserMapper;
import org.cyberrealm.tech.mapper.impl.UserMapperImpl;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.RoleRepository;
import org.cyberrealm.tech.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MANAGER_ROLE.setRole(Role.RoleName.ROLE_MANAGER);

        Set<Role> roles = new HashSet<>();
        roles.add(MANAGER_ROLE);

        FIRST_USER.setId(FIRST_USER_ID);
        FIRST_USER.setFirstName(FIRST_USER_FIRST_NAME);
        FIRST_USER.setLastName(FIRST_USER_LAST_NAME);
        FIRST_USER.setEmail(FIRST_USER_EMAIL);
        FIRST_USER.setPassword(FIRST_USER_PASSWORD);
        FIRST_USER.setRoles(roles);
    }

    @Test
    void register_validRequest_returnsUserResponse() throws RegistrationException {
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(FIRST_USER);
        when(userRepository.findByEmail(FIRST_USER.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.of(CUSTOMER_ROLE));
        when(passwordEncoder.encode(FIRST_USER.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(FIRST_USER)).thenReturn(FIRST_USER);

        UserResponseDto actualResponse = userService.register(USER_REGISTRATION_REQUEST_DTO);

        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(FIRST_USER.getEmail());
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(1)).encode(FIRST_USER_PASSWORD);
        verify(userRepository, times(1)).save(FIRST_USER);
        verify(userMapper, times(1)).toUserResponse(FIRST_USER);
    }

    @Test
    void register_existingEmail_throwsRegistrationException() {
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(FIRST_USER);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(FIRST_USER));

        assertThrows(RegistrationException.class, () ->
                userService.register(USER_REGISTRATION_REQUEST_DTO));

        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(FIRST_USER.getEmail());
        verify(roleRepository, times(0)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    void update_validRequest_returnsUpdatedUserResponse() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(FIRST_USER));
        when(roleRepository.findByRole(any(Role.RoleName.class)))
                .thenReturn(Optional.of(CUSTOMER_ROLE));
        when(userRepository.save(any(User.class))).thenReturn(FIRST_USER);

        UserResponseDto actualResponse = userService.update(FIRST_USER_ID, USER_ROLE_UPDATE_DTO);

        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_MANAGER);
        verify(userRepository, times(1)).save(FIRST_USER);
        verify(userMapper, times(1)).toUserResponse(FIRST_USER);
    }

    @Test
    void update_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(FIRST_USER_ID,
                USER_ROLE_UPDATE_DTO));

        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(0)).findByRole(any(Role.RoleName.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    void findById_existingUser_returnsUserResponse() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(FIRST_USER));

        UserResponseDto actualResponse = userService.findById(FIRST_USER_ID);

        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).toUserResponse(FIRST_USER);
    }

    @Test
    void findById_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(FIRST_USER_ID));

        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    void updateUserById_existingUser_returnsUpdatedUserResponse() {
        UserInfoUpdateDto userInfoUpdateDto = new UserInfoUpdateDto(
                NEW_FIRST_NAME,
                NEW_LAST_NAME
        );

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(FIRST_USER));
        doNothing().when(userMapper).updateUser(FIRST_USER, userInfoUpdateDto);

        UserResponseDto actualResponse = userService.updateUserById(FIRST_USER_ID,
                userInfoUpdateDto);

        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).updateUser(FIRST_USER, userInfoUpdateDto);
        verify(userMapper, times(1)).toUserResponse(FIRST_USER);
    }

    @Test
    @DisplayName("Register should throw EntityNotFoundException when default role not found")
    void register_roleNotFound_throwsEntityNotFoundException() {
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(FIRST_USER);
        when(userRepository.findByEmail(FIRST_USER.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.register(USER_REGISTRATION_REQUEST_DTO));
        String actual = exception.getMessage();
        String expected = "Role: ROLE_CUSTOMER not found";
        assertThat(actual).isEqualTo(expected);
        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(FIRST_USER.getEmail());
        verify(roleRepository, times(1)).findByRole(DEFAULT_ROLE);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Update should throw EntityNotFoundException when new role not found")
    void update_newRoleNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(FIRST_USER));
        when(roleRepository.findByRole(any(Role.RoleName.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.update(FIRST_USER_ID, USER_ROLE_UPDATE_DTO));
        String actual = exception.getMessage();
        String expected = "Role: " + USER_ROLE_UPDATE_DTO.newRole() + " not found";
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(1))
                .findByRole(Role.RoleName.valueOf(USER_ROLE_UPDATE_DTO.newRole()));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("UpdateUserById should throw EntityNotFoundException when user not found")
    void updateUserById_nonExistingUser_throwsEntityNotFoundException() {
        UserInfoUpdateDto userInfoUpdateDto = new UserInfoUpdateDto(
                NEW_FIRST_NAME,
                NEW_LAST_NAME
        );

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserById(FIRST_USER_ID, userInfoUpdateDto));
        String actual = exception.getMessage();
        String expected = "Can't find user by id:" + FIRST_USER_ID;
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(0))
                .updateUser(any(User.class), any(UserInfoUpdateDto.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Get user by ID should return correct userResponseDto")
    void getUserById_existingUser_returnsUserResponseDto() {
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(FIRST_USER));

        UserResponseDto actual = userService.findById(FIRST_USER_ID);

        assertThat(actual).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
    }

    @Test
    @DisplayName("Get user by ID should throw EntityNotFoundException when user not found")
    void getUserById_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findById(FIRST_USER_ID));
        String actual = exception.getMessage();
        String expected = "Can't find user by id:" + FIRST_USER_ID;
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
    }

}
