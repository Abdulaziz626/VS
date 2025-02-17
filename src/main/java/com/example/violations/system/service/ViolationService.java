//package com.example.violations.system.service;
//
//import com.example.violations.system.dto.ViolationRequestDto;
//import com.example.violations.system.dto.ViolationResponseDto;
//import com.example.violations.system.entity.CarImage;
//import com.example.violations.system.entity.User;
//import com.example.violations.system.entity.Violation;
//import com.example.violations.system.mapper.ViolationMapper;
//import com.example.violations.system.repository.ViolationRepository;
//import com.example.violations.system.util.AuthUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ViolationService {
//
//    private final ViolationRepository violationRepository;
//    private final ViolationMapper violationMapper;
//    private final AuthUtil authUtil;
//    private final MinioService minioService;
//
//    public Violation createViolation(ViolationRequestDto request, List<MultipartFile> carPhotos) {
//        log.info("Creating a new violation record");
//
//        User authenticatedInspector = authUtil.getAuthenticatedUser();
//        Violation violation = violationMapper.toEntity(request);
//        violation.setInspector(authenticatedInspector);
//        violation.setInspectorName(authenticatedInspector.getFullName());
//        violation.setRegion(authenticatedInspector.getRegion());
//        violation.setStatus(Violation.ViolationStatus.PENDING);
//
//        if (violation.getCarImage() == null) {
//            violation.setCarImage(new ArrayList<>());
//        }
//
//        if (carPhotos != null && !carPhotos.isEmpty()) {
//            for (MultipartFile carPhoto : carPhotos) {
//                if (carPhoto != null && !carPhoto.isEmpty()) {
//                    String photoUrl = minioService.uploadFile(carPhoto, violation);
//                    CarImage carImage = new CarImage();
//                    carImage.setImagePath(photoUrl);
//                    violation.getCarImage().add(carImage);
//                    log.info("Photo uploaded successfully: {}", photoUrl);
//                }
//            }
//        }
//
//        return violationRepository.save(violation);
//    }
//
//    public ViolationResponseDto mapToDto(Violation createdViolation) {
//        log.debug("Mapping Violation: {} to ViolationResponseDto", createdViolation.getId());
//        return violationMapper.toDto(createdViolation);
//    }
//
//    public void updateViolationStatus(Integer id, User authenticatedUser, Violation.ViolationStatus status) {
//
//        Violation violation = violationRepository.findById(id).orElse(null);
//        if (violation == null) {
//            log.warn("Violation ID {} not found", id);
//            throw new IllegalStateException("Violation not found");
//        }
//        if (authenticatedUser.getRegion() == null || !authenticatedUser.getRegion().equals(violation.getRegion())) {
//            log.warn("Unauthorized attempt to update violation ID {} by User ID {} for region {}",
//                    id, authenticatedUser.getId(), authenticatedUser.getRegion());
//            throw new IllegalArgumentException("User is not authorized to modify this violation");
//        }
//        violation.setStatus(status);
//        violationRepository.save(violation);
//        log.info("Violation ID {} status successfully updated to {}", id, status);
//    }
//
//}

package com.example.violations.system.service;

import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.CarImage;
import com.example.violations.system.entity.User;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.mapper.ViolationMapper;
import com.example.violations.system.repository.ViolationRepository;
import com.example.violations.system.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final ViolationMapper violationMapper;
    private final AuthUtil authUtil;
    private final MinioService minioService;

    public Violation createViolation(ViolationRequestDto request, List<MultipartFile> carPhotos) {
        log.info("Creating a new violation record");

        User authenticatedInspector = authUtil.getAuthenticatedUser();

        Violation violation = violationMapper.toEntity(request);
        violation.setInspector(authenticatedInspector);
        violation.setInspectorName(authenticatedInspector.getFullName());
        violation.setRegion(authenticatedInspector.getRegion());
        violation.setStatus(Violation.ViolationStatus.PENDING);
        if (violation.getCarImage() == null) {
            violation.setCarImage(new ArrayList<>());
        }
        if (carPhotos != null && !carPhotos.isEmpty()) {
            List<String> photoUrls = minioService.uploadFile(carPhotos, violation);

            for (String photoUrl : photoUrls) {
                CarImage carImage = new CarImage();
                carImage.setImagePath(photoUrl);
                carImage.setViolation(violation);
                violation.getCarImage().add(carImage);
            }

            log.info("All photos uploaded successfully: {}", photoUrls);
        }
        return violationRepository.save(violation);
    }

    public ViolationResponseDto mapToDto(Violation createdViolation) {
        log.debug("Mapping Violation: {} to ViolationResponseDto", createdViolation.getId());
        return violationMapper.toDto(createdViolation);
    }

    public void updateViolationStatus(Integer id, User authenticatedUser, Violation.ViolationStatus status) {

        Violation violation = violationRepository.findById(id).orElse(null);
        if (violation == null) {
            log.warn("Violation ID {} not found", id);
            throw new IllegalStateException("Violation not found");
        }
        if (authenticatedUser.getRegion() == null || !authenticatedUser.getRegion().equals(violation.getRegion())) {
            log.warn("Unauthorized attempt to update violation ID {} by User ID {} for region {}",
                    id, authenticatedUser.getId(), authenticatedUser.getRegion());
            throw new IllegalArgumentException("User is not authorized to modify this violation");
        }
        violation.setStatus(status);
        violationRepository.save(violation);
        log.info("Violation ID {} status successfully updated to {}", id, status);
    }

}

