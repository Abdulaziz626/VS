package com.example.violations.system.repository;

import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Integer> {


    List<Violation> findByRegion(Region region);
    long countByRegion(Region region);

    List<Violation> findByStatus(Violation.ViolationStatus status);
}
