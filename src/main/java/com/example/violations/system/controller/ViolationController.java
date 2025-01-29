package com.example.violations.system.controller;

import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.UserRepository;
import com.example.violations.system.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationService violationService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('INSPECTOR')")
    public ResponseEntity<Violation> createViolation(@RequestBody ViolationRequestDto dto) {
        Violation response = violationService.createViolation(dto);
        return ResponseEntity.ok(response);
    }
}








