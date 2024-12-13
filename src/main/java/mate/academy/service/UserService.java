package mate.academy.service;

import mate.academy.dto.user.UserInfoUpdateDto;
import mate.academy.dto.user.UserRegistrationRequestDto;
import mate.academy.dto.user.UserResponseDto;
import mate.academy.dto.user.UserRoleUpdateDto;
import mate.academy.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto update(Long id, UserRoleUpdateDto requestDto);

    UserResponseDto findById(Long id);

    UserResponseDto updateUserById(Long id, UserInfoUpdateDto requestDto);
}
