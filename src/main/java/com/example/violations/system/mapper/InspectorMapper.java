package com.example.violations.system.mapper;

import com.example.violations.system.dto.InspectorDto;
import com.example.violations.system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class InspectorMapper {

    public User toInspector(InspectorDto inspectorDto) {
       User inspector = new User();
        inspector.setId(inspector.getId());
        inspector.setEmail(inspector.getEmail());
        inspector.setFullName(inspector.getFullName());
        return inspector;
    }
}