package com.zacharyscheer.volleyballstattracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    // --- Core Security Beans ---

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Temporary UserDetailsService (Fixed for both Login and JWT) ---

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            if ("testuser@volleyball.com".equals(username)) {
                // When loading the user for ANY purpose (Login OR JWT validation),
                // we MUST provide the *encoded* password so the login provider can compare it.
                // We use passwordEncoder().encode("password") to generate the encoded hash every time.
                return org.springframework.security.core.userdetails.User
                        .withUsername("testuser@volleyball.com")
                        // REVERTED FIX: The password must be encoded for the login to work!
                        .password(passwordEncoder().encode("password"))
                        .authorities("USER")
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build();
            }
            throw new UsernameNotFoundException("User not found: " + username);
        };
    }

    // --- Authentication Provider ---

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}

