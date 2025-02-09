package com.example.violations.system.controller;

import com.example.violations.system.dto.InspectorDto;
import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.ViolationRepository;
import com.example.violations.system.service.ViolationPdfService;
import com.example.violations.system.service.ViolationService;
import com.example.violations.system.service.ViolationXlsxService;
import com.example.violations.system.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationRepository violationRepository;
    private final ViolationService violationService;
    private final AuthUtil authUtil;
    private final ViolationXlsxService violationXlsxService;
    private final ViolationPdfService violationPdfService;
    private static final String REPORT_FILE_NAME = "violations_report.pdf";
    private static final Logger logger = LoggerFactory.getLogger(ViolationController.class);
    private static final String ARABIC_NUMERALS = "٠١٢٣٤٥٦٧٨٩";
    private static final String ENGLISH_NUMERALS = "0123456789";



    @PostMapping("/create")
    @PreAuthorize("hasAuthority('INSPECTOR')")
    public ResponseEntity<ViolationResponseDto> createViolation(
            @RequestPart("data") ViolationRequestDto dto,
            @RequestPart(value = "carPhoto", required = false) MultipartFile carPhoto) {
        Violation violation = violationService.createViolation(dto, carPhoto);
        ViolationResponseDto response = violationService.mapToDto(violation);
        return ResponseEntity.ok(response);
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

    @GetMapping("/violation_types")
    @PreAuthorize("hasAnyAuthority('INSPECTOR','OPERATOR')")
    public ResponseEntity<String> getAvailableViolationTypes() {
        List<String> violationTypes = Arrays.stream(Violation.ViolationType.values())
                .map(Enum::name)
                .toList();

        String response = "Violation types:\n" +
                IntStream.range(0, violationTypes.size())
                        .mapToObj(i -> (i + 1) + ". " + violationTypes.get(i))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void exportViolationsToExcel(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=violations.xlsx");

        try {
            violationXlsxService.exportViolationsToExcel(response);
        } catch (IOException e) {
            response.reset();
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Failed to generate the Excel file. Please try again later.");
            } catch (IOException innerEx) {

            }
        }
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void exportViolationsToPdf(HttpServletResponse response) {
        violationPdfService.exportViolationsToPdf(response);
    }

    @GetMapping("/view/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getViolationsWithFilter(
            @RequestParam(required = false) Violation.ViolationStatus status,
            @RequestParam(required = false) String regionName) {
        if (status == null && regionName == null) {
            return ResponseEntity.badRequest().body("Please provide at least one filter: 'status' or 'regionName'.");
        }
        List<Violation> violations;
        try {
            if (regionName != null) {
                Region region = Region.valueOf(regionName);
                if (status != null) {
                    violations = violationRepository.findByRegionAndStatus(region, status);
                } else {
                    violations = violationRepository.findByRegion(region);
                }
            } else {
                violations = violationRepository.findByStatus(status);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid region name provided.");
        }

        if (violations.isEmpty()) {
            return ResponseEntity.ok("No violations found for the given filters.");
        }
        List<ViolationResponseDto> response = violations.stream()
                .map(this::toViolationDto)
                .toList();

        return ResponseEntity.ok(response);
    }
    private ViolationResponseDto toViolationDto(Violation violation) {
        ViolationResponseDto dto = new ViolationResponseDto();
        dto.setId(violation.getId());
        dto.setDescription(violation.getDescription());
        dto.setStatus(violation.getStatus());
        dto.setViolationDate(violation.getViolationDate());
        dto.setViolationLocation(violation.getViolationLocation());
        dto.setPlateNumber(violation.getPlateNumber());
        dto.setViolationType(violation.getViolationType());
        dto.setRegion(violation.getRegion());
        dto.setInspector(toInspectorDto(violation.getInspector()));
        return dto;
    }
    private InspectorDto toInspectorDto(User inspector) {
        if (inspector == null) {
            return null;
        }
        InspectorDto dto = new InspectorDto();
        dto.setId(inspector.getId());
        dto.setEmail(inspector.getEmail());
        dto.setFullName(inspector.getFullName());
        return dto;
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('INSPECTOR','OPERATOR')")
    public ResponseEntity<?> getAllViolations(HttpServletRequest request) {
        User authenticatedUser = authUtil.getAuthenticatedUser();
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(violation -> isAuthorizedToView(authenticatedUser, violation)) // Filter by region
                .toList();
        String baseUrl = getBaseUrl(request);
        List<ViolationResponseDto> response = violations.stream()
                .map(violation -> toViolationDto(violation, baseUrl)) // Include full photo URL in response
                .toList();
        return ResponseEntity.ok(response);
    }
    private ViolationResponseDto toViolationDto(Violation violation, String baseUrl) {
        ViolationResponseDto dto = new ViolationResponseDto();
        dto.setId(violation.getId());
        dto.setDescription(violation.getDescription());
        dto.setStatus(violation.getStatus());
        dto.setViolationDate(violation.getViolationDate());
        dto.setViolationLocation(violation.getViolationLocation());
        dto.setPlateNumber(violation.getPlateNumber());
        dto.setViolationType(violation.getViolationType());
        dto.setRegion(violation.getRegion());
        dto.setInspector(toInspectorDto(violation.getInspector()));
        if (violation.getCarPhotoUrl() != null) {
            dto.setCarPhotoUrl(baseUrl + "/uploads/" + violation.getCarPhotoUrl());
        }

        return dto;
    }
    private String getBaseUrl(HttpServletRequest request) {
        String port = toEnglishNumerals(String.valueOf(request.getServerPort()));
        return String.format("%s://%s:%s", request.getScheme(), request.getServerName(), port);
    }

    private String toEnglishNumerals(String input) {
        return input.chars()
                .mapToObj(c -> {
                    int index = ARABIC_NUMERALS.indexOf((char) c);
                    return index >= 0 ? ENGLISH_NUMERALS.charAt(index) : (char) c;
                })
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private boolean isAuthorizedToView(User authenticatedUser, Violation violation) {
        if (authenticatedUser.getRegion() == null ||
                !authenticatedUser.getRegion().equals(violation.getRegion())) {
            logger.warn("Inspector (ID: {}) attempted to view violation ID {} outside their region (Region: {}).",
                    authenticatedUser.getId(), violation.getId(), violation.getRegion());
            return false;
        }
        return true;
    }

}










