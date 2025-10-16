package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.dto.AuthenticationResponse;
import com.zacharyscheer.volleyballstattracker.dto.LoginRequest; // We need to create this DTO next!
import com.zacharyscheer.volleyballstattracker.dto.RegisterRequest;
import com.zacharyscheer.volleyballstattracker.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication requests (register and login).
 * These endpoints are public and manage user access to the application.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Used to inject the AuthenticationService
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationService authService;
    // NOTE: We no longer need AuthenticationManager, UserDetailsService, or JwtService here.
    // The service handles those dependencies, simplifying the controller.

    /**
     * POST /api/auth/register : Public endpoint to create a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // Delegate all registration logic (encoding, saving, token generation) to the service
        return ResponseEntity.ok(authService.register(request));
    }


    /**
     * POST /api/auth/login : Public endpoint to authenticate an existing user and issue a JWT.
     * This method requires the AuthenticationService to implement the login logic.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        // Delegate all login logic (authentication, token generation) to the service
        return ResponseEntity.ok(authService.login(request));
    }


    // -------------------------------------------------------------
    // SECURED TEST ENDPOINT (Kept for integration testing)
    // -------------------------------------------------------------
    /**
     * GET /api/auth/secured
     * This endpoint requires a valid JWT token. It tests the JWT filter chain.
     */
    @GetMapping("/secured")
    public ResponseEntity<String> securedEndpoint() {
        // If execution reaches this point, the user is authenticated by the JWT filter.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok("SUCCESS! Hello, " + username + "! Your security filter is working perfectly.");
    }
}
