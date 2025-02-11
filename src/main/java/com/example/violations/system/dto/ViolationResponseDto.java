package com.example.violations.system.dto;

import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Violation;
import lombok.Data;
import java.util.Date;

@Data
public class ViolationResponseDto {
    private Integer id;
    private String description;
    private String violationLocation;
    private String plateNumber;
    private Violation.ViolationType violationType;
    private Region region;
    private InspectorDto inspector;
    private String inspectorNotes;
    private Date violationDate;
    private Violation.ViolationStatus status;
    private String carPhotoUrl;
    private String presignedUrl;


}