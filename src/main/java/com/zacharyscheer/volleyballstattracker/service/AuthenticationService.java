package com.zacharyscheer.volleyballstattracker.service;

import com.zacharyscheer.volleyballstattracker.Security.JwtService;
import com.zacharyscheer.volleyballstattracker.dto.AuthenticationResponse;
import com.zacharyscheer.volleyballstattracker.dto.LoginRequest;
import com.zacharyscheer.volleyballstattracker.dto.RegisterRequest;
import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Dependency needed for login()

    /**
     * Creates a new user, encodes their password, and generates a JWT.
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Build the User object (using the simplified User entity)
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Authenticates an existing user and generates a JWT.
     * This is the method that was missing and causing the error.
     */
    public AuthenticationResponse login(LoginRequest request) {
        // 1. Authenticate credentials using the AuthenticationManager.
        // If this fails (wrong password or email), an exception is thrown.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If authentication succeeded, retrieve the fully loaded User entity from the DB.
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Should never happen if authentication passed

        // 3. Generate the token
        var jwtToken = jwtService.generateToken(user);

        // 4. Return the token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}