package com.zacharyscheer.volleyballstattracker.config;

import com.zacharyscheer.volleyballstattracker.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Main security configuration for the application.
 * This class enables Web Security, defines access rules,
 * and integrates the custom JWT filter chain.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // These components are automatically injected by Spring
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Defines the security filter chain rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configure CORS and disable CSRF (essential for stateless APIs)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // 2. Define endpoint access rules
                .authorizeHttpRequests(auth -> auth
                        // Allow access to the authentication controller endpoints (like /api/auth/login)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Allow unauthenticated POST requests to create a new player (if desired)
                        .requestMatchers(HttpMethod.POST, "/api/player").permitAll()

                        // Allow unauthenticated GET requests to view players (e.g., for roster view)
                        .requestMatchers(HttpMethod.GET, "/api/player/**").permitAll()

                        // 💥 FINAL FIX: Require COACH authority for POST /api/matches
                        .requestMatchers(HttpMethod.POST, "/api/matches").hasAuthority("COACH")

                        // Allow all other authenticated requests to /api/matches/{id} etc.
                        .requestMatchers("/api/matches/**").authenticated()

                        // Require authentication for all other endpoints
                        .anyRequest().authenticated()
                )

                // 3. Configure session management to be stateless (JWT is used instead of server sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Set the custom authentication provider
                .authenticationProvider(authenticationProvider)

                // 5. Add the JWT filter *before* the standard Spring Security username/password filter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Configures CORS to allow all origins, methods, and headers for development simplicity.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Set to specific client origins in a production environment
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}