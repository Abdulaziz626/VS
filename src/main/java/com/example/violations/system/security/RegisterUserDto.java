package com.example.violations.system.security;

import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Role;
import lombok.Data;

@Data
public class RegisterUserDto {
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private Region region; // New field added to handle user region
}