package com.example.violations.system.mapper;

import com.example.violations.system.dto.InspectorDto;
import com.example.violations.system.dto.ViolationRequestDto;
import com.example.violations.system.dto.ViolationResponseDto;
import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.User;
import org.springframework.stereotype.Component;
@Component
public class ViolationMapper {

    public Violation toEntity(ViolationRequestDto dto) {
        Violation violation = new Violation();
        violation.setDescription(dto.getDescription());
        violation.setViolationLocation(dto.getLocation()); // Updated to match renamed field
        violation.setPlateNumber(dto.getPlateNumber());
        violation.setViolationType(dto.getViolationType());
        violation.setInspectorNotes(dto.getInspectorNotes());
        return violation;
    }

        public ViolationResponseDto toDto(Violation violation) {
            ViolationResponseDto dto = new ViolationResponseDto();
            dto.setId(violation.getId());
            dto.setDescription(violation.getDescription());
            dto.setViolationLocation(violation.getViolationLocation());
            dto.setPlateNumber(violation.getPlateNumber());
            dto.setViolationType(violation.getViolationType());
            dto.setRegion(violation.getRegion());
            dto.setInspectorNotes(violation.getInspectorNotes());
            dto.setViolationDate(violation.getViolationDate());
            dto.setStatus(violation.getStatus());
            dto.setCarPhotoUrl(violation.getCarPhotoUrl());

            InspectorDto inspectorDto = new InspectorDto();
            inspectorDto.setId(violation.getInspector().getId());
            inspectorDto.setFullName(violation.getInspector().getFullName());
            inspectorDto.setEmail(violation.getInspector().getEmail());

            dto.setInspector(inspectorDto);

            return dto;
        }



}