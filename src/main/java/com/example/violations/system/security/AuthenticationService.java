package com.example.violations.system.security;

import com.example.violations.system.entity.Role;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Region;
import com.example.violations.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto registerUserDto) {
        log.info("signup");
        if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        Role role;

        try {
            role = Role.valueOf(registerUserDto.getRole().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided.");
        }

        User user = new User();
        user.setFullName(registerUserDto.getFullName());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(role);
        user.setRegion(registerUserDto.getRegion());// Assign role directly from the enum
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        log.info("login");
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }

        return user;
    }
}
