package com.example.violations.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "violations")
public class Violation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "violation_location")
    private String violationLocation;

    @Column(nullable = false, name = "plate_number")
    private String plateNumber;

    @OneToMany(cascade = CascadeType.ALL,
    mappedBy = "violation", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarImage> carImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationType violationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @ManyToOne
    @JoinColumn(nullable = false, name = "inspector_id")
    private User inspector;

    @Column(name = "inspector_name")
    private String inspectorName;

    @Column(name = "inspector_notes")
    private String inspectorNotes;

    @CreationTimestamp
    @Column(updatable = false, name = "violation_date")
    private Date violationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationStatus status;

    public enum ViolationStatus {
        PENDING, ACCEPTED, REJECTED
    }

    public enum ViolationType {
        SPEEDING, USING_PHONE, RED_LIGHT_CROSSING, WRONG_PARKING
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public Violation() {
        this.expiredAt = this.createdAt.plusDays(30);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }



}