//package com.example.violations.system.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.util.Date;
//
//@Data
//@Setter
//@Getter
//@Entity
//@Table(name = "violations")
//public class Violation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(nullable = false)
//    private Integer id;
//
//    @Column(nullable = false)
//    private String description;
//
//
//    public boolean isInspector(User user) {
//        return user.getRole() == Role.INSPECTOR;
//    }
//
//    @Column(name = "inspector_id", nullable = false)
//    private Long inspectorId;
//
//    @ManyToOne
//    @JoinColumn(name = "inspector_id", nullable = false)
//    private User inspector; // Adjust as necessary
//
//
//    @ManyToOne
//    @JoinColumn(name = "region_id", nullable = false)
//    private Region region;  // Linking the Violation to a specific Region
//
//    @CreationTimestamp
//    @Column(updatable = false, name = "created_at")
//    private Date createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private Date updatedAt;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ViolationStatus status = ViolationStatus.PENDING;  // Enum to track the status of the violation
//
//    public enum ViolationStatus {
//        PENDING,
//        REVIEWED,
//        ACCEPTED,
//        REJECTED
//    }
//
//    // Additional methods as needed
//}
