package com.example.violations.system.repository;

import com.example.violations.system.entity.CarImage;
import com.example.violations.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {
}
