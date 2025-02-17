package com.example.violations.system.dto;

import com.example.violations.system.entity.CarImage;
import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Violation;
import lombok.Data;
import java.util.Date;
import java.util.List;

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
    private List<String> carImages;
    private List<String> presignedUrl;


}