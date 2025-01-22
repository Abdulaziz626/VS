package com.example.violations.system.dto;

import com.example.violations.system.entity.Role;
import lombok.Data;

@Data
public class RegisterUserDto {
    private String fullName;
    private String email;
    private String password;
    private Role role; // Use Role enum instead of String
//    private String region;
}
