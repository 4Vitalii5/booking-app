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
import static org.cyberrealm.tech.util.TestConstants.NEW_ROLE;
import static org.cyberrealm.tech.util.TestUtil.CUSTOMER_ROLE;
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
import org.cyberrealm.tech.dto.user.UserRegistrationRequestDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.dto.user.UserRoleUpdateDto;
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

    private User user;
    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto userResponseDto;
    private UserRoleUpdateDto userRoleUpdateDto;
    private Role role;

    @BeforeEach
    void setUp() {
        Set<Role> roles = new HashSet<>();
        roles.add(CUSTOMER_ROLE);

        user = new User();
        user.setId(FIRST_USER_ID);
        user.setEmail(FIRST_USER_EMAIL);
        user.setPassword(FIRST_USER_PASSWORD);
        user.setFirstName(FIRST_USER_FIRST_NAME);
        user.setLastName(FIRST_USER_LAST_NAME);
        user.setRoles(roles);

        userRegistrationRequestDto = new UserRegistrationRequestDto(
                FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD,
                FIRST_USER_PASSWORD,
                FIRST_USER_FIRST_NAME,
                FIRST_USER_LAST_NAME
        );

        userResponseDto = new UserResponseDto(
                FIRST_USER_ID,
                FIRST_USER_EMAIL,
                FIRST_USER_FIRST_NAME,
                FIRST_USER_LAST_NAME
        );

        userRoleUpdateDto = new UserRoleUpdateDto(NEW_ROLE);

        role = new Role();
        role.setRole(Role.RoleName.ROLE_CUSTOMER);
    }

    @Test
    void register_validRequest_returnsUserResponse() throws RegistrationException {
        when(userMapper.toModel(userRegistrationRequestDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(user.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDto actualResponse = userService.register(userRegistrationRequestDto);

        assertThat(actualResponse).isEqualTo(userResponseDto);
        verify(userMapper, times(1)).toModel(userRegistrationRequestDto);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(1)).encode(FIRST_USER_PASSWORD);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    void register_existingEmail_throwsRegistrationException() {
        when(userMapper.toModel(userRegistrationRequestDto)).thenReturn(user);
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        assertThrows(RegistrationException.class, () ->
                userService.register(userRegistrationRequestDto));

        verify(userMapper, times(1)).toModel(userRegistrationRequestDto);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(0)).findByRole(Role.RoleName.ROLE_CUSTOMER);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    void update_validRequest_returnsUpdatedUserResponse() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(any(Role.RoleName.class))).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto actualResponse = userService.update(FIRST_USER_ID, userRoleUpdateDto);

        assertThat(actualResponse).isEqualTo(userResponseDto);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(1)).findByRole(Role.RoleName.ROLE_MANAGER);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    void update_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(FIRST_USER_ID,
                userRoleUpdateDto));

        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(0)).findByRole(any(Role.RoleName.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    void findById_existingUser_returnsUserResponse() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        UserResponseDto actualResponse = userService.findById(FIRST_USER_ID);

        assertThat(actualResponse).isEqualTo(userResponseDto);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).toUserResponse(user);
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

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(user, userInfoUpdateDto);

        UserResponseDto actualResponse = userService.updateUserById(FIRST_USER_ID,
                userInfoUpdateDto);

        assertThat(actualResponse).isEqualTo(userResponseDto);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(userMapper, times(1)).updateUser(user, userInfoUpdateDto);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    @DisplayName("Register should throw EntityNotFoundException when default role not found")
    void register_roleNotFound_throwsEntityNotFoundException() {
        when(userMapper.toModel(userRegistrationRequestDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRole(DEFAULT_ROLE)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.register(userRegistrationRequestDto));
        String actual = exception.getMessage();
        String expected = "Role: ROLE_CUSTOMER not found";
        assertThat(actual).isEqualTo(expected);
        verify(userMapper, times(1)).toModel(userRegistrationRequestDto);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(roleRepository, times(1)).findByRole(DEFAULT_ROLE);
        verify(passwordEncoder, times(0)).encode(any(String.class));
        verify(userRepository, times(0)).save(any(User.class));
        verify(userMapper, times(0)).toUserResponse(any(User.class));
    }

    @Test
    @DisplayName("Update should throw EntityNotFoundException when new role not found")
    void update_newRoleNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(any(Role.RoleName.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.update(FIRST_USER_ID, userRoleUpdateDto));
        String actual = exception.getMessage();
        String expected = "Role: " + userRoleUpdateDto.newRole() + " not found";
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).findById(FIRST_USER_ID);
        verify(roleRepository, times(1))
                .findByRole(Role.RoleName.valueOf(userRoleUpdateDto.newRole()));
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
        when(userRepository.findById(FIRST_USER_ID)).thenReturn(Optional.of(user));

        UserResponseDto actual = userService.findById(FIRST_USER_ID);

        assertThat(actual).isEqualTo(userResponseDto);
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
