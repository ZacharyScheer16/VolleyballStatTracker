package com.zacharyscheer.volleyballstattracker.config;

import com.zacharyscheer.volleyballstattracker.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. DISABLE CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. DEFINE AUTHORIZATION RULES (FIXED)
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to /api/auth/** (login/register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // All other requests MUST be authenticated
                        .anyRequest().authenticated()
                )


                // 3. CONFIGURE SESSION MANAGEMENT
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. CONFIGURE AUTHENTICATION PROVIDER
                .authenticationProvider(authenticationProvider)

                // 5. ADD JWT FILTER
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
