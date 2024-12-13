package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserInfoUpdateDto(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {
}
