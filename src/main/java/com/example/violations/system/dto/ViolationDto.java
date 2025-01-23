package com.example.violations.system.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ViolationDto {
    private Integer id; // Matches the ID type from your entity class
    private String description; // Description of the violation
    private String status; // Status of the violation (e.g., PENDING, ACCEPTED, REJECTED)
    private String location; // Location of the violation
    private String plateNumber; // License plate number
    private String violationType; // Type of violation (e.g., SPEEDING, PHONE_USAGE, RED_LIGHT)
    private String inspectorName; // Name of the inspector
    private String region; // Region where the violation occurred
    private String inspectorNotes; // Additional notes from the inspector
    private Date violationDate; // Date the violation occurred
    private Date updatedAt; // Date the violation was last updated
}
