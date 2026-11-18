package com.hotel.soa.authentication.service;

import com.hotel.soa.authentication.entity.User;
import com.hotel.soa.authentication.entity.User.UserRole;
import com.hotel.soa.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    @Transactional
    public User register(String username, String password, String email, String roleStr) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        UserRole role = UserRole.CUSTOMER;
        if (roleStr != null) {
            try {
                role = UserRole.valueOf(roleStr.toUpperCase().trim());
            } catch (Exception ignored) {}
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();

        return userRepository.save(user);
    }
}
