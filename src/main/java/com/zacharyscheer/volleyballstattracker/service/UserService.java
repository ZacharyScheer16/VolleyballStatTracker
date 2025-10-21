package com.zacharyscheer.volleyballstattracker.service;

import com.zacharyscheer.volleyballstattracker.dto.PasswordChangeRequest;
import com.zacharyscheer.volleyballstattracker.models.User;
import com.zacharyscheer.volleyballstattracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email ));
    }

    public void changePassword(Long userId, PasswordChangeRequest passwordChangeRequest){
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        String newPassword = passwordEncoder.encode(passwordChangeRequest.getNewPassword());
        user.setPassword(newPassword);

        userRepository.save(user);
    }
}
