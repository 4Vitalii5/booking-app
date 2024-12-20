package org.cyberrealm.tech.service;

import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserRegistrationRequestDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.dto.user.UserRoleUpdateDto;
import org.cyberrealm.tech.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto update(Long id, UserRoleUpdateDto requestDto);

    UserResponseDto findById(Long id);

    UserResponseDto updateUserById(Long id, UserInfoUpdateDto requestDto);
}
