package com.example.violations.system.controller;

import com.example.violations.system.dto.InspectorDto;
import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Role;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.ViolationRepository;
import com.example.violations.system.service.MinioService;
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
import java.util.Optional;
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
    private final MinioService minioService;

@PostMapping("/create")
@PreAuthorize("hasAuthority('INSPECTOR')")
public ResponseEntity<ViolationResponseDto> createViolation(
        @RequestPart("data") ViolationRequestDto dto,
        @RequestPart(value = "carPhoto", required = false) MultipartFile carPhoto) {
    try {
        Violation violation = violationService.createViolation(dto, carPhoto);

        String URL = null;
        if (carPhoto != null) {
            URL = minioService.uploadFile(carPhoto, violation);
        }
        ViolationResponseDto response = violationService.mapToDto(violation);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        logger.error("Error creating violation", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
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
        dto.setInspectorNotes(violation.getInspectorNotes());
        dto.setViolationLocation(violation.getViolationLocation());
        dto.setPlateNumber(violation.getPlateNumber());
        dto.setViolationType(violation.getViolationType());
        dto.setRegion(violation.getRegion());
        dto.setInspector(toInspectorDto(violation.getInspector()));
        dto.setCarImage(violation.getCarImage());
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
    @PreAuthorize("hasAnyAuthority('ADMIN','INSPECTOR','OPERATOR')")
    public ResponseEntity<?> getAllViolations(HttpServletRequest request) {
        User authenticatedUser = authUtil.getAuthenticatedUser();
        List<Violation> violations = violationRepository.findAll().stream()
                .filter(violation -> isAuthorizedToView(authenticatedUser, violation))
                .toList();

        List<ViolationResponseDto> response = violations.stream()
                .map(this::toViolationDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private boolean isAuthorizedToView(User authenticatedUser, Violation violation) {

        if ("ADMIN".equals(authenticatedUser.getRole().name())) {
            return true;
        }
        if (authenticatedUser.getRegion() == null ||
                !authenticatedUser.getRegion().equals(violation.getRegion())) {
            logger.warn("Inspector (ID: {}) attempted to view violation ID {} outside their region (Region: {}).",
                    authenticatedUser.getId(), violation.getId(), violation.getRegion());
            return false;
        }
        return true;
    }

    @GetMapping("/violation_types")
    @PreAuthorize("hasAnyAuthority('INSPECTOR','OPERATOR')")
    public ResponseEntity<String> getAllViolationTypes() {
        List<String> violationTypes = Arrays.stream(Violation.ViolationType.values())
                .map(Enum::name)
                .toList();

        String response = "The Violation types:\n" +
                IntStream.range(0, violationTypes.size())
                        .mapToObj(i -> (i + 1) + ". " + violationTypes.get(i))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/Regions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> getAllRegions() {
        List<String> Regions = Arrays.stream(Region.values())
                .map(Enum::name)
                .toList();

        String response = "The Regions:\n" +
                IntStream.range(0, Regions.size())
                        .mapToObj(i -> (i + 1) + ". " + Regions.get(i))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/Roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> getAllRoles() {
        List<String> Roles = Arrays.stream(Role.values())
                .map(Enum::name)
                .toList();

        String response = "The Roles:\n" +
                IntStream.range(0, Roles.size())
                        .mapToObj(i -> (i + 1) + ". " + Roles.get(i))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/presigned_url")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSPECTOR','OPERATOR')")
    public ResponseEntity<?> getPresignedUrl(@RequestParam String violationId) {

            User authenticatedUser = authUtil.getAuthenticatedUser();
            Optional<Violation> violationOptional = violationRepository.findById(Integer.parseInt(violationId));
            if (violationOptional.isEmpty() ||
                    !isAuthorizedToView(authenticatedUser, violationOptional.get())) {
                return ResponseEntity.status(403).body("Unauthorized to view this violation");
            }
            String presignedUrl = minioService.createPresignedUrl("Violation_" + violationId);
            return ResponseEntity.ok(presignedUrl);
    }

    @GetMapping("/view_by_ID")
    @PreAuthorize("hasAnyAuthority('ADMIN','INSPECTOR','OPERATOR')")
    public ResponseEntity<?> viewById(@RequestParam String objectName, HttpServletRequest request) {

        User authenticatedUser = authUtil.getAuthenticatedUser();
        Optional<Violation> violationOptional = violationRepository.findById(Integer.parseInt(objectName));

        if (violationOptional.isEmpty() ||
                !isAuthorizedToView(authenticatedUser, violationOptional.get())) {
            return ResponseEntity.status(403).body("Unauthorized to view this violation");
        }
        Violation violation = violationOptional.get();
        ViolationResponseDto responseDto = toViolationDto(violation);
        String presignedUrl = minioService.createPresignedUrl("Violation_" + violation.getId());

        responseDto.setPresignedUrl(presignedUrl);
        return ResponseEntity.ok(responseDto);
    }


}










