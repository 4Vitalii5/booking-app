package org.cyberrealm.tech.repository;

import java.util.Optional;
import org.cyberrealm.tech.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Role.RoleName role);
}
