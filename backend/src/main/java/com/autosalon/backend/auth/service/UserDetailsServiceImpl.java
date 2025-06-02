package com.autosalon.backend.auth.service;

import com.autosalon.backend.auth.repository.AuthAccountRepository;
import com.autosalon.backend.general.entity.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthAccountRepository authAccountRepository;

    public UserDetailsServiceImpl(AuthAccountRepository authAccountRepository) {
        this.authAccountRepository = authAccountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = authAccountRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с логином: " + login));

        return UserDetailsImpl.build(account);
    }
}
