package com.autosalon.backend.auth.repository;

import java.util.Optional;

import com.autosalon.backend.general.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AuthAccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByLogin(String login);

    Boolean existsByLogin(String login);

    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET login = :newLogin WHERE login = :oldLogin", nativeQuery = true)
    int updateLoginInAccount(String oldLogin, String newLogin);
}
