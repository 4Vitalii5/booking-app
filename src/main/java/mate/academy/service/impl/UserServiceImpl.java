package mate.academy.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserInfoUpdateDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.dto.user.UserRoleUpdateDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE = Role.RoleName.ROLE_CUSTOMER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        User user = userMapper.toModel(requestDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: "
                    + user.getEmail()
                    + " already exists");
        }
        Role role = roleRepository.findByRole(DEFAULT_ROLE).orElseThrow(
                () -> new EntityNotFoundException("Role: " + DEFAULT_ROLE + " not found")
        );
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponseDto update(Long id, UserRoleUpdateDto requestDto) {
        User user = getUserById(id);
        Role.RoleName newRoleName = Role.RoleName.valueOf(requestDto.newRole());
        Role newRole = roleRepository.findByRole(newRoleName).orElseThrow(
                () -> new EntityNotFoundException("Role: " + requestDto.newRole() + " not found")
        );
        Set<Role> roles = user.getRoles();
        roles.add(newRole);
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = getUserById(id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateUserById(Long id, UserInfoUpdateDto requestDto) {
        User user = getUserById(id);
        userMapper.updateUser(user, requestDto);
        return userMapper.toUserResponse(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id:" + id)
        );
    }
}
