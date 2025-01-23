package com.example.violations.system.service;

import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.Role;
//import com.example.violations.system.exception.UnauthorizedException;
import com.example.violations.system.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {

    private final ViolationRepository violationRepository;

    @Autowired
    public ViolationService(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    public Violation saveViolation(Violation violation) {
        return violationRepository.save(violation);
    }

    public List<Violation> getViolationsByInspector(Long inspectorId) {
        return violationRepository.findByInspectorId(inspectorId);
    }

    public Violation getViolationById(Integer id) {
        return violationRepository.findById(id).orElseThrow(() -> new RuntimeException("Violation not found"));
    }

    public void deleteViolation(Integer id) {
        violationRepository.deleteById(id);
    }
}

