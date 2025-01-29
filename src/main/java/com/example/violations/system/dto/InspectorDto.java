package com.example.violations.system.dto;

import lombok.Data;
import com.example.violations.system.entity.User;

@Data
public class InspectorDto {

    private Long id;
    private String email;
    private String fullName;
}
