package com.example.violations.system.security;

import com.example.violations.system.entity.Role;
import lombok.Data;

@Data
public class RegisterUserDto {
    private String fullName;
    private String email;
    private String password;
    private Role role;

}
