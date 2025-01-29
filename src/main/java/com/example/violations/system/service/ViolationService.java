package com.example.violations.system.service;

import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.mapper.ViolationMapper;
import com.example.violations.system.repository.UserRepository;
import com.example.violations.system.repository.ViolationRepository;
import com.example.violations.system.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final UserRepository userRepository;
    private final ViolationMapper violationMapper;
    private final UserDetailsService userDetailsService;
    private final AuthUtil authUtil;

    public Violation createViolation(ViolationRequestDto request) {
        log.info("createViolation");
        User user = authUtil.getAuthenticatedUser();
        Violation violation = violationMapper.toEntity(request);
        violation.setInspector(user);
        violation.setInspectorName(user.getFullName());
        violation.setRegion(user.getRegion());
        violation.setStatus(Violation.ViolationStatus.PENDING);

        return violationRepository.save(violation);
    }

}