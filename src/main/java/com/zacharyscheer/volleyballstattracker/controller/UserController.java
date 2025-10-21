package com.zacharyscheer.volleyballstattracker.controller;

import com.zacharyscheer.volleyballstattracker.dto.PasswordChangeRequest;
import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody PasswordChangeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        // This should not happen if the user is authenticated, but is a good safeguard.
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Authenticated user not found.");
        }

        // 3. Call the service layer method to perform the secure change
        try {
            userService.changePassword(Long.valueOf(user.getId()), request);
            return ResponseEntity.ok("Password updated successfully.");

        } catch (IllegalArgumentException e) {
            // Catches the "Incorrect current password." exception from the service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            // Catch any other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during password change.");
        }
    }
}
