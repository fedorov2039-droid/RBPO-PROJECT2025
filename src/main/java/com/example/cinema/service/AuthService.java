package com.example.cinema.service;

import com.example.cinema.model.AppUser;
import com.example.cinema.model.Role;
import com.example.cinema.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) {
        if (password.length() < 8 || !password.matches(".*[!@#$%^&*].*")) {
            throw new RuntimeException("Пароль должен быть > 8 символов и содержать (!@#$%^&*)");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Пользователь уже существует");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Шифруем пароль
        user.setRole(Role.ROLE_USER);

        if ("admin".equals(username)) {
            user.setRole(Role.ROLE_ADMIN);
        }

        userRepository.save(user);
    }
}