package com.example.violations.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Setter
@Getter
@Entity
@Table(name = "violations")
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location; // Location of the violation

    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber; // License plate (1-3 letters + 1-4 numbers)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "violation_type")
    private ViolationType violationType; // Enum for violation type

    public enum ViolationType {
        SPEEDING,
        PHONE_USAGE,
        RED_LIGHT
    }

    @ManyToOne
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector; // The inspector responsible for the violation

    @Column(name = "inspector_notes")
    private String inspectorNotes; // Additional notes from the inspector

    @Column(updatable = false, name = "region")
    private String region;

    @CreationTimestamp
    @Column(updatable = false, name = "violation_date")
    private Date violationDate; // Date of the violation

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt; // Timestamp for updates

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationStatus status = ViolationStatus.PENDING; // Status of the violation

    public enum ViolationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public boolean isInspector(User user) {
        return user.getRole() == Role.INSPECTOR;
    }
}
