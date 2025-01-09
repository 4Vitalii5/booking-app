package org.cyberrealm.tech.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cyberrealm.tech.service.impl.UserServiceImpl.DEFAULT_ROLE;
import static org.cyberrealm.tech.util.TestConstants.DUPLICATE_EMAIL_MESSAGE;
import static org.cyberrealm.tech.util.TestConstants.ENCODED_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.ENTITY_NOT_FOUND_EXCEPTION;
import static org.cyberrealm.tech.util.TestConstants.FIRST_ROLE_NAME;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_ID;
import static org.cyberrealm.tech.util.TestConstants.FIRST_USER_PASSWORD;
import static org.cyberrealm.tech.util.TestConstants.NEW_FIRST_NAME;
import static org.cyberrealm.tech.util.TestConstants.NEW_LAST_NAME;
import static org.cyberrealm.tech.util.TestConstants.ROLE_NOT_FOUND;
import static org.cyberrealm.tech.util.TestConstants.USER_STRING;
import static org.cyberrealm.tech.util.TestUtil.USER_REGISTRATION_REQUEST_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_RESPONSE_DTO;
import static org.cyberrealm.tech.util.TestUtil.USER_ROLE_UPDATE_DTO;
import static org.cyberrealm.tech.util.TestUtil.getCustomerRole;
import static org.cyberrealm.tech.util.TestUtil.getFirstUser;
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

    @Test
    @DisplayName("Register new user")
    void register_validRequest_returnsUserResponse() throws RegistrationException {
        //Given
        User user = getFirstUser();
        Role role = getCustomerRole();
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);

        //When
        UserResponseDto actualResponse = userService.register(USER_REGISTRATION_REQUEST_DTO);

        //Then
        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(1)).encode(FIRST_USER_PASSWORD);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Throw RegistrationException if email already exists")
    void register_existingEmail_throwsRegistrationException() {
        //Given
        User user = getFirstUser();
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(user);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        //When
        RegistrationException exception = assertThrows(RegistrationException.class, () ->
                userService.register(USER_REGISTRATION_REQUEST_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(DUPLICATE_EMAIL_MESSAGE, user.getEmail());
        assertThat(actual).isEqualTo(expected);
        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(0)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Update user roles with valid request")
    void update_validRequest_returnsUpdatedUserResponse() {
        //Given
        Role role = getCustomerRole();
        User user = getFirstUser();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(any(Role.RoleName.class))).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        //When
        UserResponseDto actualResponse = userService.update(FIRST_USER_ID, USER_ROLE_UPDATE_DTO);

        //Then
        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_MANAGER);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Throw EntityNoFoundException if user does`t exists")
    void update_nonExistingUser_throwsEntityNotFoundException() {
        //Given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //When
        assertThrows(EntityNotFoundException.class, () -> userService.update(FIRST_USER_ID,
                USER_ROLE_UPDATE_DTO));

        //Then
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(0)).findByRole(any(Role.RoleName.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Return user by ID")
    void findById_existingUser_returnsUserResponse() {
        //Given
        User user = getFirstUser();
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        //When
        UserResponseDto actualResponse = userService.findById(FIRST_USER_ID);

        //Then
        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Update user info by ID")
    void updateUserById_existingUser_returnsUpdatedUserResponse() {
        //Given
        User user = getFirstUser();
        UserInfoUpdateDto userInfoUpdateDto = new UserInfoUpdateDto(
                NEW_FIRST_NAME,
                NEW_LAST_NAME
        );
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(user, userInfoUpdateDto);

        //When
        UserResponseDto actualResponse = userService.updateUserById(FIRST_USER_ID,
                userInfoUpdateDto);

        //Then
        assertThat(actualResponse).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).updateUser(user, userInfoUpdateDto);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Register should throw EntityNotFoundException when default role not found")
    void register_roleNotFound_throwsEntityNotFoundException() {
        //Given
        User user = getFirstUser();
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.register(USER_REGISTRATION_REQUEST_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ROLE_NOT_FOUND, FIRST_ROLE_NAME);
        assertThat(actual).isEqualTo(expected);
        verify(userMapper, times(1)).toModel(USER_REGISTRATION_REQUEST_DTO);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(1)).findByRole(DEFAULT_ROLE);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Update should throw EntityNotFoundException when new role not found")
    void update_newRoleNotFound_throwsEntityNotFoundException() {
        //Given
        User user = getFirstUser();
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(any(Role.RoleName.class))).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.update(FIRST_USER_ID, USER_ROLE_UPDATE_DTO));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ROLE_NOT_FOUND, USER_ROLE_UPDATE_DTO.newRole());
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
        //Given
        UserInfoUpdateDto userInfoUpdateDto = new UserInfoUpdateDto(NEW_FIRST_NAME, NEW_LAST_NAME);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserById(FIRST_USER_ID, userInfoUpdateDto));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, USER_STRING, FIRST_USER_ID);
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(0))
                .updateUser(any(User.class), any(UserInfoUpdateDto.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Get user by ID should return correct userResponseDto")
    void getUserById_existingUser_returnsUserResponseDto() {
        //Given
        User user = getFirstUser();
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(user));

        //When
        UserResponseDto actual = userService.findById(FIRST_USER_ID);

        //Then
        assertThat(actual).isEqualTo(USER_RESPONSE_DTO);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
    }

    @Test
    @DisplayName("Get user by ID should throw EntityNotFoundException when user not found")
    void getUserById_nonExistingUser_throwsEntityNotFoundException() {
        //Given
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.empty());

        //When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.findById(FIRST_USER_ID));
        String actual = exception.getMessage();

        //Then
        String expected = String.format(ENTITY_NOT_FOUND_EXCEPTION, USER_STRING, FIRST_USER_ID);
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
    }
}
