package com.example.violations.system.dto;

import com.example.violations.system.entity.Region;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.Violation.ViolationType;
import lombok.Data;

@Data
public class ViolationRequestDto {
    private String description;
    private String location;
    private String plateNumber;
    private ViolationType violationType;
    private String inspectorNotes;

}
