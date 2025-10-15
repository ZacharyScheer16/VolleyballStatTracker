package com.zacharyscheer.volleyballstattracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents a user in the system. This simplified entity is used for
 * authentication and assumes a single, shared level of access (COACH).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email; // Used as the unique username for login

    @Column(nullable = false)
    private String password; // MUST be stored as a BCrypt-encoded hash

    // *** NOTE: The 'role' field is intentionally removed for simplicity. ***

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Hardcode the single authority for all authenticated users
        return List.of(new SimpleGrantedAuthority("COACH"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Default returns true for all account statuses
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
