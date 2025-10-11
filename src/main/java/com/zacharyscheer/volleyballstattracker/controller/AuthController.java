package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.Security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication requests (login and token generation).
 * This endpoint is public and issues the JWT token needed for protected access.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    // Spring injects the necessary components we defined in ApplicationConfig and JwtService
    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    // A simple DTO (Data Transfer Object) for receiving the login request body
    record AuthRequest(String username, String password) {}
    // A simple DTO for sending the JWT token back to the client
    record AuthResponse(String token) {}

    /**
     * POST /api/auth/login : Public endpoint to authenticate a user and issue a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        // 1. Authenticate the user credentials using the AuthenticationManager
        // If the username/password is wrong, this line throws an exception and the login fails.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // 2. If authentication succeeded, load the user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        // 3. Generate the JWT token using the JwtService
        final String jwt = jwtService.generateToken(userDetails);

        // 4. Return the token to the client (the iPad/computer app)
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
