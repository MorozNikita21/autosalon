package com.autosalon.backend.auth.service;

import com.autosalon.backend.general.entity.Account;
import com.autosalon.backend.general.entity.ERole;
import com.autosalon.backend.general.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {

    @Test
    void testConstructorAndGettersFlags() {
        SimpleGrantedAuthority authClient = new SimpleGrantedAuthority("ROLE_CLIENT");
        SimpleGrantedAuthority authAdmin  = new SimpleGrantedAuthority("ROLE_ADMIN");
        List<SimpleGrantedAuthority> auths = List.of(authClient, authAdmin);

        UserDetailsImpl user = new UserDetailsImpl(
                "john",
                "pwd123",
                auths
        );

        assertEquals("john", user.getUsername());
        assertEquals("pwd123", user.getPassword());

        Collection<? extends SimpleGrantedAuthority> actualAuths =
                user.getAuthorities().stream()
                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toList());
        assertTrue(actualAuths.contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
        assertTrue(actualAuths.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testBuildFromAccount() {
        Account account = new Account();
        account.setLogin("mia");
        account.setPassword("secret");

        Role roleClient = new Role();
        roleClient.setName(ERole.CLIENT);
        Role roleAdmin = new Role();
        roleAdmin.setName(ERole.ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(roleClient);
        roles.add(roleAdmin);
        account.setRoles(roles);

        UserDetailsImpl built = UserDetailsImpl.build(account);

        Collection<? extends SimpleGrantedAuthority> builtAuths =
                built.getAuthorities().stream()
                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toList());
        assertTrue(builtAuths.contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
        assertTrue(builtAuths.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertEquals("mia", built.getUsername());
        assertEquals("secret", built.getPassword());

        assertTrue(built.isAccountNonExpired());
        assertTrue(built.isAccountNonLocked());
        assertTrue(built.isCredentialsNonExpired());
        assertTrue(built.isEnabled());
    }
}
