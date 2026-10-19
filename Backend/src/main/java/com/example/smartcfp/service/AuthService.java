package com.example.smartcfp.service;

import com.example.smartcfp.model.User;
import com.example.smartcfp.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String signup(
            String name,
            String email,
            String password) {

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalArgumentException(
                    "All fields are required"
            );
        }

        email = email.trim().toLowerCase(
                java.util.Locale.ROOT
        );

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(password);

        User user = new User(
                name.trim(),
                email,
                encodedPassword
        );

        userRepository.save(user);

        return "Account created successfully";
    }

    public String login(
            String email,
            String password) {

        if (email == null || password == null) {
            throw new IllegalArgumentException(
                    "Email and password are required"
            );
        }

        User user = userRepository
                .findByEmail(
                        email.trim().toLowerCase(
                                java.util.Locale.ROOT
                        )
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        return "Login successful";
    }
}