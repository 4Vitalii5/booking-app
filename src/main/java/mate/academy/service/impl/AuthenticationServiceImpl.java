package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.user.UserLoginRequestDto;
import mate.academy.dto.user.UserLoginResponseDto;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;

    @Override
    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        return null;
    }
}
