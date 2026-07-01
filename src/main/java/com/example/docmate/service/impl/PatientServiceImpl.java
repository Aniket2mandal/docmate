package com.example.docmate.service.impl;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.response.PatientResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.service.PatientService;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public GlobalResponse getAllPatient() {
        List<PatientEntity> patientEntityList = patientRepository.findAllActivePatients();

        List<PatientResponse> patientResponseList = patientEntityList.stream()
                .map(patient -> {
                    PatientResponse patientResponse = modelMapper.map(patient, PatientResponse.class);
                    patientResponse.setPatientId(patient.getId());
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

    @Override
    public  GlobalResponse deletePatient(String patientId){

        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient"+ patientId+ MyConstants.ERR_MSG_NOT_FOUND ));

        List<AppointmentEntity> appointmentEntityList = appointmentRepository
                .findByPatientId(patientEntity.getId());
        for(AppointmentEntity appointmentEntity : appointmentEntityList){
            if(appointmentEntity.getStatus() == AppointmentStatus.BOOKED){
                throw new GlobalException("Cannot delete a patient with booked appointment", HttpStatus.BAD_REQUEST);
            }
        }
        appointmentRepository.deleteAll(appointmentEntityList);
        patientRepository.delete(patientEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Patient deleted successfully");
    }
}
