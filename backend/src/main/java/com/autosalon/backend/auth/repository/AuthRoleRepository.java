package com.autosalon.backend.auth.repository;

import java.util.Optional;

import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
