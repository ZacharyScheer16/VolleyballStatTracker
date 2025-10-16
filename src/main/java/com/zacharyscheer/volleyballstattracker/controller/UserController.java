package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.repository.UserRepository;
import com.zacharyscheer.volleyballstattracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/user/profile : Retrieves the currently authenticated user's details.
     * Requires a valid JWT token.
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        // 1. Get the username (email) from the JWT in the Security Context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Fetch the user details from the service
        User user = userService.findByEmail(email);

        // NOTE: The password field is hidden by Spring's JSON serialization by default,
        // but if it shows, you might want to create a separate DTO for the response.
        return ResponseEntity.ok(user);
    }
}
