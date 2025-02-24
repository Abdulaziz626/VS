package com.example.violations.system.repository;

import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.Region;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Integer> {


    List<Violation> findByRegion(Region region);
    List<Violation> findByStatus(Violation.ViolationStatus status);

    @NotNull
    @Override
    Optional<Violation> findById(@NotNull Integer integer);


    @Query("SELECT v FROM Violation v")
    Stream<Violation> streamAll();

    @Query("SELECT v FROM Violation v LEFT JOIN FETCH v.carImage WHERE v.id = :id")
    Optional<Violation> findByIdWithCarImages(@Param("id") Integer id);


    @Query("SELECT v FROM Violation v LEFT JOIN FETCH v.carImage WHERE v.region = :region AND v.status = :status")
    List<Violation> findByRegionAndStatus(@Param("region") Region region, @Param("status") Violation.ViolationStatus status);


    void deleteByExpiredAtBefore(LocalDateTime now);




}
