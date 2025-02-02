package com.example.violations.system.controller;


import com.example.violations.system.dto.InspectorDto;
import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.UserRepository;
import com.example.violations.system.repository.ViolationRepository;
import com.example.violations.system.service.ViolationService;
import com.example.violations.system.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationRepository violationRepository;
    private final ViolationService violationService;
    private final AuthUtil authUtil;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('INSPECTOR')")
    public ResponseEntity<ViolationResponseDto> createViolation(@RequestBody ViolationRequestDto dto) {
        Violation createdViolation = violationService.createViolation(dto);
        ViolationResponseDto response = violationService.mapToDto(createdViolation);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('INSPECTOR','OPERATOR')")
    public ResponseEntity<?> getViolationsInUserRegion() {
        User authenticatedUser = authUtil.getAuthenticatedUser();
        Region userRegion = authenticatedUser.getRegion();
        if (userRegion == null) {
            return ResponseEntity.status(403).body("You are not assigned to any region.");
        }
        List<Violation> violations = violationRepository.findByRegion(userRegion);
        if (violations.isEmpty()) {
            return ResponseEntity.ok("You don't have any violations in your region.");
        }
        return ResponseEntity.ok(violations);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OPERATOR')")
    public ResponseEntity<Void> updateViolationStatus(@PathVariable Integer id, @RequestParam Violation.ViolationStatus status) {
        User authenticatedUser = authUtil.getAuthenticatedUser();
        if (status != Violation.ViolationStatus.ACCEPTED && status != Violation.ViolationStatus.REJECTED) {
            return ResponseEntity.badRequest().build();
        }
        try {
            violationService.updateViolationStatus(id, authenticatedUser, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(403).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}








