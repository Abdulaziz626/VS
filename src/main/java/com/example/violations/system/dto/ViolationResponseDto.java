package com.example.violations.system.dto;

import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Role;
import com.example.violations.system.entity.Violation;
import lombok.Data;
import org.springframework.security.core.userdetails.User;

import java.util.Date;

import static com.example.violations.system.entity.Violation.*;

@Data
public class ViolationResponseDto {
    private Integer id;
    private String description;
    private String violationLocation;
    private String plateNumber;
    private Violation.ViolationType violationType;
    private Region region;
    private InspectorDto inspector; // استخدم DTO بدلاً من كائن `User`
    private String inspectorNotes;
    private Date violationDate;
    private Violation.ViolationStatus status;
    private String carPhotoUrl;

}