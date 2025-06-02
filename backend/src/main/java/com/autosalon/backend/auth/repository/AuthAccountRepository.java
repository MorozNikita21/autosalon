package com.autosalon.backend.auth.repository;

import java.util.Optional;

import com.autosalon.backend.general.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByLogin(String login);
    Boolean existsByLogin(String login);
}
