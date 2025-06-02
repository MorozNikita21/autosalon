package com.autosalon.backend.auth.service;

import com.autosalon.backend.auth.dto.RegisterRequest;
import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.auth.repository.AuthClientRepository;
import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {

    private final AuthAccountRepository authAccountRepository;
    private final AuthClientRepository authClientRepository;

    public AuthService(AuthAccountRepository authAccountRepository,
                       AuthClientRepository authClientRepository) {
        this.authAccountRepository = authAccountRepository;
        this.authClientRepository = authClientRepository;
    }

    @Transactional
    public void createClientProfile(Account account, RegisterRequest signUpRequest) {
        Client client = new Client();
        client.setLogin(account.getLogin());
        client.setAccount(account);
        client.setName(signUpRequest.getName());
        client.setEmail(signUpRequest.getEmail());
        client.setBirthday(LocalDate.parse(signUpRequest.getBirthday()));
        client.setAddress(signUpRequest.getAddress());
        client.setPassport(signUpRequest.getPassport());
        client.setDriverLicense(signUpRequest.getDriverLicense());
        client.setFirstLicenseDate(LocalDate.parse(signUpRequest.getFirstLicenseDate()));
        authClientRepository.save(client);
    }

}
