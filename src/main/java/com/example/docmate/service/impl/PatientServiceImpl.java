package com.example.docmate.service.impl;

import com.example.docmate.entity.PatientEntity;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.response.PatientResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    @Override
    public GlobalResponse getAllPatient() {
        List<PatientEntity> patientEntityList = patientRepository.findAll();

        List<PatientResponse> patientResponseList = patientEntityList.stream()
                .map(patient -> {
                    PatientResponse patientResponse = modelMapper.map(patient, PatientResponse.class);
                    if (patient.getUser() != null) {
                        UserResponse userResponse = modelMapper.map(patient.getUser(), UserResponse.class);
                        if (patient.getUser().getRole() != null) {
                            RoleResponse roleResponse = modelMapper.map(patient.getUser().getRole(), RoleResponse.class);
                            userResponse.setRole(roleResponse.getName());
                        }
                        patientResponse.setUser(userResponse);
                    }
                    return patientResponse;
                }).toList();

        return GlobalResponseBuilder.buildSuccessResponseWithData("All patient fetched", patientResponseList);
    }
}
