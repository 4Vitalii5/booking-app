package mate.academy.dto.user;

import jakarta.validation.constraints.NotBlank;
import mate.academy.model.Role;
import mate.academy.validation.ValidEnum;

public record UserRoleUpdateDto(
        @NotBlank
        @ValidEnum(enumClass = Role.RoleName.class, message = "Invalid role name")
        String newRole
) {
}
