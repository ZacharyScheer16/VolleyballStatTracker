package com.zacharyscheer.volleyballstattracker.Security;

import com.zacharyscheer.volleyballstattracker.Security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that executes once per request to check for a JWT in the Authorization header.
 * If found and valid, it sets the user's authentication context for the session.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check for JWT format: "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Exit if no token is found
        }

        // 2. Extract the JWT string (skipping "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Use JwtService to safely extract the username
        username = jwtService.extractUsername(jwt);

        // 4. If username is valid AND the request is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load UserDetails (from ApplicationConfig's in-memory store)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validate token (signature and expiration)
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Create and set the authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Mark this request as fully authenticated!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Pass the request to the next filter/controller
        filterChain.doFilter(request, response);
    }
}