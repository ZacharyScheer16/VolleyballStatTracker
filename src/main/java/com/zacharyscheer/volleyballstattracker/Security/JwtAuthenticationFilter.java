package com.zacharyscheer.volleyballstattracker.Security; // Check this package name!

import com.zacharyscheer.volleyballstattracker.Security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

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
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check for Authorization header and format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract JWT and username
        jwt = authHeader.substring(7);
        // Note: Your JWT Service should have a method to extract the username (subject)
        username = jwtService.extractUsername(jwt);

        // 3. Check if user is present and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details (authorities) from the temporary UserDetailsService
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 5. Create Authentication Token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are null for JWT
                        userDetails.getAuthorities()
                );

                // Set authentication details (important for session management, even if stateless)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set Security Context (CRITICAL FIX)
                // This is what tells Spring Security the user is now authenticated for this request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}