//package com.example.violations.system.repository;
//
//import com.example.violations.system.entity.Violation;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.List;
//
//public interface ViolationRepository extends JpaRepository<Violation, Long> {
//    List<Violation> findByInspector(User inspector);
//    ; // Get violations by inspector ID
//    List<Violation> findByRegionId(Long regionId);       // Get violations by region ID
//    List<Violation> findByStatus(String status);         // Get violations by status
//}
//
