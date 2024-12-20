package org.cyberrealm.tech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.dto.user.UserInfoUpdateDto;
import org.cyberrealm.tech.dto.user.UserResponseDto;
import org.cyberrealm.tech.dto.user.UserRoleUpdateDto;
import org.cyberrealm.tech.model.User;
import org.cyberrealm.tech.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Managing authentication and user registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/role")
    @Operation(summary = "Update user roles",
            description = "Enables users to update their roles, "
                    + "providing role-based access.")
    public UserResponseDto updateRoles(@PathVariable Long id,
                                       @RequestBody @Valid UserRoleUpdateDto requestDto) {
        return userService.update(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @GetMapping("/me")
    @Operation(summary = "Get profile information", description = "Retrieves the profile "
            + "information for the currently logged-in user.")
    public UserResponseDto getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.findById(user.getId());
    }

    @PreAuthorize("hasRole('MANAGER') OR hasRole('CUSTOMER')")
    @PatchMapping("/me")
    @Operation(summary = "Update user profile", description = "Allows users to update their "
            + "profile information.")
    public UserResponseDto updateUserInfo(Authentication authentication,
                                          @RequestBody @Valid UserInfoUpdateDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateUserById(user.getId(), requestDto);
    }
}
