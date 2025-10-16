package com.zacharyscheer.volleyballstattracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharyscheer.volleyballstattracker.controller.AuthController;
import com.zacharyscheer.volleyballstattracker.dto.AuthenticationResponse;
import com.zacharyscheer.volleyballstattracker.dto.LoginRequest;
import com.zacharyscheer.volleyballstattracker.dto.RegisterRequest;
import com.zacharyscheer.volleyballstattracker.Security.JwtService;
import com.zacharyscheer.volleyballstattracker.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager; // Added Mock dependency
import org.springframework.security.core.userdetails.UserDetailsService; // Added Mock dependency
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                // Exclude general security config to avoid loading full context
                SecurityAutoConfiguration.class,
        }
)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mock the controller's immediate dependency ---
    @MockBean
    private AuthenticationService authService;

    // --- Mocks required for Spring Security context/filter chain ---
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean // CRITICAL FIX: Required to correctly process unauthenticated requests (401)
    private AuthenticationManager authenticationManager;

    // --- Common Mock Data ---
    private final String MOCK_TOKEN = "mocked.jwt.token";
    private final String TEST_EMAIL = "test@gcu.edu";
    private final String TEST_PASSWORD = "password123";

    @Test
    void register_shouldReturnTokenAndStatus200() throws Exception {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(MOCK_TOKEN)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(authResponse)));
    }

    @Test
    void login_shouldReturnTokenAndStatus200() throws Exception {
        // Arrange
        LoginRequest loginRequest = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(MOCK_TOKEN)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(authResponse)));
    }

    @Test
    @WithMockUser(username = TEST_EMAIL) // Simulates an authenticated user
    void securedEndpoint_shouldReturnSuccessMessageAndStatus200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/secured"))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS! Hello, " + TEST_EMAIL + "! Your security filter is working perfectly."));
    }
}