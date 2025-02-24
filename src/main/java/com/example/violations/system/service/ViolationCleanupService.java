package com.example.violations.system.service;

import com.example.violations.system.repository.ViolationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ViolationCleanupService {

    private final ViolationRepository violationRepository;

    public ViolationCleanupService(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredViolations() {
        violationRepository.deleteByExpiredAtBefore(LocalDateTime.now());
    }
}
