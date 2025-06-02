package com.autosalon.backend.auth.repository;

import com.autosalon.backend.general.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String login);
}
