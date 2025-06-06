package com.autosalon.backend.auth.repository;

import java.util.Optional;

import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AuthRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE account_role SET account_login = :newLogin WHERE account_login = :oldLogin",
            nativeQuery = true
    )
    int updateAccountLoginInAccountRole(String oldLogin, String newLogin);
}
