package org.cyberrealm.tech.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.cyberrealm.tech.model.Role;
import org.cyberrealm.tech.validation.ValidEnum;

public record UserRoleUpdateDto(
        @NotBlank
        @ValidEnum(enumClass = Role.RoleName.class, message = "Invalid role name")
        String newRole
) {
}
