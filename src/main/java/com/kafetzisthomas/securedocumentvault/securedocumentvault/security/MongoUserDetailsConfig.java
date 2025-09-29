package com.kafetzisthomas.securedocumentvault.securedocumentvault.security;

import com.kafetzisthomas.securedocumentvault.securedocumentvault.dao.UserRepository;
import com.kafetzisthomas.securedocumentvault.securedocumentvault.entity.AppUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

@Configuration
public class MongoUserDetailsConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            AppUser u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return User.withUsername(u.getUsername())
                    .password(u.getPassword())
                    .authorities(u.getRoles() == null ? java.util.List.of()
                            : u.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                    .disabled(!u.isEnabled())
                    .build();
        };
    }

}
