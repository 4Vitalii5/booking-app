package org.cyberrealm.tech.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserRegistrationRequestDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.dto.user.UserRoleUpdateDto;
import org.cyberrealm.tech.exception.EntityNotFoundException;
import org.cyberrealm.tech.exception.RegistrationException;
import org.cyberrealm.tech.mapper.UserMapper;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.repository.RoleRepository;
import org.cyberrealm.tech.repository.UserRepository;
import org.cyberrealm.tech.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final Role.RoleName DEFAULT_ROLE = Role.RoleName.ROLE_CUSTOMER;
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
                    + user.getEmail() + " already exists");
        }
        Role role = roleRepository.findByRole(DEFAULT_ROLE).orElseThrow(() ->
                new EntityNotFoundException("Role: " + DEFAULT_ROLE + " not found")
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
        Role newRole = roleRepository.findByRole(newRoleName).orElseThrow(() ->
                new EntityNotFoundException("Role: " + requestDto.newRole() + " not found")
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
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find user by id:" + id)
        );
    }
}
