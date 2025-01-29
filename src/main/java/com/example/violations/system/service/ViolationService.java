package com.example.violations.system.service;

import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.mapper.ViolationMapper;
import com.example.violations.system.repository.ViolationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final ViolationMapper violationMapper;

    public Violation createViolation(ViolationRequestDto request) {
        Violation violation = violationMapper.toEntity(request);
        violation.setStatus(Violation.ViolationStatus.PENDING);

        return violationRepository.save(violation);
    }
}