package com.example.violations.system.controller;

import com.example.violations.system.security.RegisterUserDto;
import com.example.violations.system.entity.User;
import com.example.violations.system.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add-user")
    @PreAuthorize("hasAuthority('ADMIN')") // Ensure only admins can add users
    public String addUser(@RequestBody RegisterUserDto registerUserDto) {
        if (userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            return "Email already exists.";
        }

        User user = new User();
        user.setFullName(registerUserDto.getFullName());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(registerUserDto.getRole());
        user.setRegion(registerUserDto.getRegion());

        userRepository.save(user);
        return "User added successfully.";
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')") // Ensure only admins can view all users
    public List<User> getAllUsers() {
        String username = getAuthenticatedUsername(); // Extracted logic to helper method
        return userRepository.findAll();
    }

    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            System.out.println(userRepository.findByEmail(((UserDetails) principal).getUsername()));
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}