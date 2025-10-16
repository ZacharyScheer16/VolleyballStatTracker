package com.zacharyscheer.volleyballstattracker;

import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.repository.UserRepository;
import com.zacharyscheer.volleyballstattracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService, focusing on isolated business logic.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final String TEST_EMAIL = "test@gcu.edu";

    @BeforeEach
    void setUp() {
        // Corrected: Removed the .role(...) call from the User builder
        testUser = User.builder()
                .id(1)
                .email(TEST_EMAIL)
                .password("encoded_password")
                .build();
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // Act
        User foundUser = userService.findByEmail(TEST_EMAIL);

        // Assert
        assertNotNull(foundUser);
        assertEquals(TEST_EMAIL, foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void findByEmail_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        // Verify that the correct exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByEmail(TEST_EMAIL);
        });
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }
}