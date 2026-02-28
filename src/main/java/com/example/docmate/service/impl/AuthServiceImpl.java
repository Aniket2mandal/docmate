package com.example.docmate.service.impl;

import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AuthService;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.springframework.boot.autoconfigure.container.ContainerImageMetadata.isPresent;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;

    public GlobalResponse registerAdmin(UserRequest user) {

//        if(user.getRoleId() != null){
//            RoleEntity roleEntity = roleRepository.findById(user.getRoleId())
//                    .orElseThrow(()-> new GlobalException("Role "+MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
//        }

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        RoleEntity roleEntity = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        userEntity.setRole(roleEntity);

        userRepository.save(userEntity);

        return GlobalResponseBuilder.buildSuccessResponse("User registered successfully");
    }

    public GlobalResponse registerPatient(PatientRequest patient) {
        UserEntity userEntity = null;
        if (!isEmpty(patient.getUser()) && patient.getUser() != null) {
            userEntity = modelMapper.map(patient.getUser(), UserEntity.class);

            RoleEntity roleEntity = roleRepository.findByName(Role.PATIENT)
                    .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            userEntity.setRole(roleEntity);

            userRepository.save(userEntity);
        }
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setAge(patient.getAge());
        patientEntity.setHeight(patient.getHeight());
        patientEntity.setWeight(patient.getWeight());
        patientEntity.setUser(userEntity);
        patientRepository.save(patientEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Patient registered successfully");
    }
}
