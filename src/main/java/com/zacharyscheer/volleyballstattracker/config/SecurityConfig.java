package com.zacharyscheer.volleyballstattracker.config;

// IMPORTANT: Keep the uppercase 'Security' if that is your package name
import com.zacharyscheer.volleyballstattracker.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    /**
     * Defines the security filter chain using modern lambda syntax.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF using lambda syntax
                .csrf(csrf -> csrf.disable())

                // 2. Define authorization rules using lambda syntax
                .authorizeHttpRequests(auth -> auth
                        // Allow access to /api/auth/** endpoints (login) WITHOUT a token.
                        .requestMatchers("/api/auth/**").permitAll()
                        // Require authentication for ALL other endpoints.
                        .anyRequest().authenticated()
                )

                // 3. Configure session management: Stateless for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Set the custom authentication provider
                .authenticationProvider(authenticationProvider)

                // 5. Add our custom JWT filter BEFORE Spring's default filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
