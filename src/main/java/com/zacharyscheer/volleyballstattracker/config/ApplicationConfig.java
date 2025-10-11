package com.zacharyscheer.volleyballstattracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the core security beans, including the AuthenticationManager,
 * the Password Encoder, and the temporary in-memory user lookup.
 */
@Configuration
public class ApplicationConfig {

    // --- TEMPORARY IN-MEMORY USER STORE ---
    private final Map<String, UserDetails> inMemoryUsers = new HashMap<>();

    // FIX: Use a local PasswordEncoder instance in the constructor to avoid circular dependency.
    public ApplicationConfig() {
        // Use a local instance of the encoder for creating the test user
        PasswordEncoder localEncoder = new BCryptPasswordEncoder();

        // Initializes a temporary user for testing the login endpoint.
        // Credentials: testuser@volleyball.com / password
        String encodedPassword = localEncoder.encode("password");
        UserDetails testUser = User.builder()
                .username("testuser@volleyball.com")
                .password(encodedPassword)
                .roles("USER")
                .build();
        inMemoryUsers.put(testUser.getUsername(), testUser);
    }
    // --- END TEMPORARY USER STORE ---

    /**
     * Defines the BCrypt algorithm for securely hashing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines how Spring should look up a user by their username (email).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserDetails user = inMemoryUsers.get(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            return user;
        };
    }

    /**
     * Defines the Authentication Provider: uses the UserDetailsService and PasswordEncoder
     * to verify the user's identity.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        // Spring is now ready to correctly retrieve the passwordEncoder bean here.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager needed to perform the login action in the AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}