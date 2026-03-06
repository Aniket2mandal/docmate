package com.example.docmate.service.impl;


import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.DoctorService;
import com.example.docmate.utils.MyConstants;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
@AllArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public GlobalResponse createDoctor(DoctorRequest doctor) {
        UserEntity userEntity = null;
        if (!isEmpty(doctor.getUser()) && doctor.getUser() != null) {
            if (userRepository.existsByEmail(doctor.getUser().getEmail())) {
                throw new GlobalException("User with email " + MyConstants.ERR_MSG_ALREADY_EXISTS, HttpStatus.CONFLICT);
            }
            RoleEntity roleEntity = roleRepository.findByName(Role.DOCTOR)
                    .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
//            userEntity=UserEntity.builder()
//                    .firstName(doctor.getUser().getFirstName())
//                    .lastName(doctor.getUser().getLastName())
//                    .email(doctor.getUser().getEmail())
//                    .address(doctor.getUser().getAddress())
//                    .phone(doctor.getUser().getPhone())
//                    .gender(doctor.getUser().getGender())
//                    .password(passwordEncoder.encode(doctor.getUser().getPassword()))
//                    .role(roleEntity)
//                    .build();
            userEntity = modelMapper.map(doctor.getUser(), UserEntity.class);
            userEntity.setPassword(passwordEncoder.encode(doctor.getUser().getPassword()));
            userEntity.setRole(roleEntity);
            userRepository.save(userEntity);

        }
//        DoctorEntity doctorEntity= DoctorEntity.builder()
//                .user(userEntity)
//                .experience(doctor.getExperience())
//                .specialization(doctor.getSpecialization())
//                .consultation_fee(doctor.getConsultation_fee())
//                .qualification(doctor.getQualification())
//                .build();
        DoctorEntity doctorEntity = modelMapper.map(doctor, DoctorEntity.class);
        doctorEntity.setUser(userEntity);
        doctorRepository.save(doctorEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Doctor created successfully");
    }

    @Override
    public GlobalResponse getAllDoctor() {
        List<DoctorEntity> doctorEntityList = doctorRepository.findAll();
        List<DoctorResponse> doctorResponseList = doctorEntityList.stream()
                .map(doctor -> {
                    DoctorResponse doctorResponse = modelMapper.map(doctor, DoctorResponse.class);
                    if (doctor.getUser() != null) {
                        UserResponse userResponse = modelMapper.map(doctor.getUser(), UserResponse.class);
                        if (doctor.getUser().getRole() != null) {
                            RoleResponse roleResponse = modelMapper.map(doctor.getUser().getRole(), RoleResponse.class);
                            userResponse.setRole(roleResponse.getName());
                        }
                        doctorResponse.setUser(userResponse);
                    }
                    return doctorResponse;
                }).toList();


        return GlobalResponseBuilder.buildSuccessResponseWithData("All doctor fetched successfully", doctorResponseList);

    }

    @Override
    public GlobalResponse getDoctorById(String id) {
        DoctorEntity doctorEntity = doctorRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        DoctorResponse doctorResponse = modelMapper.map(doctorEntity, DoctorResponse.class);
        if (doctorEntity.getUser() != null) {
            UserResponse userResponse = modelMapper.map(doctorEntity.getUser(), UserResponse.class);
            if (doctorEntity.getUser().getRole() != null) {
                RoleResponse roleResponse = modelMapper.map(doctorEntity.getUser().getRole(), RoleResponse.class);
                userResponse.setRole(roleResponse.getName());
            }
            doctorResponse.setUser(userResponse);
        }
        return GlobalResponseBuilder.buildSuccessResponseWithData("Doctor fetched successfully", doctorResponse);
    }
    @Override
    public GlobalResponse changeStatus(UserRequest user, String id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        userEntity.setStatus(user.getStatus());
        userRepository.save(userEntity);
        return  GlobalResponseBuilder.buildSuccessResponse("User status changed");
    }

}
