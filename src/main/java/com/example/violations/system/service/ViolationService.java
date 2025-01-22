//package com.example.violations.system.service;
//
//import com.example.violations.system.entity.User;
//import com.example.violations.system.entity.Violation;
//import com.example.violations.system.entity.Role;
//import com.example.violations.system.exception.UnauthorizedException;
//import com.example.violations.system.repository.ViolationRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ViolationService {
//
//    @Autowired
//    private ViolationRepository violationRepository;
//
//    public boolean isInspector(User user) {
//        return user.getRole() == Role.INSPECTOR;
//    }
//
//    public void addViolation(User user, Violation violation) {
//        if (isInspector(user)) {
//            // Logic to add the violation
//            violationRepository.save(violation);
//        } else {
//            throw new UnauthorizedException("Only inspectors can add violations.");
//        }
//    }
//}
