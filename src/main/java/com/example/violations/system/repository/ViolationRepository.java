package com.example.violations.system.repository;

import com.example.violations.system.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Integer> {

    // Custom query methods if needed

    // Find violations by a specific inspector ID
    List<Violation> findByInspectorId(Long inspectorId);

    // Find violations by plate number
    List<Violation> findByPlateNumber(String plateNumber);

    // Find violations by status
    List<Violation> findByStatus(Violation.ViolationStatus status);

    // Find violations by location
    List<Violation> findByLocation(String location);

    // Find violations by region
    List<Violation> findByRegion(String region);
}
