package com.example.docmate.service.impl;

import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.LoginRequest;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.LoginResponse;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AuthService;
import com.example.docmate.utils.JwtUtils;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.springframework.boot.autoconfigure.container.ContainerImageMetadata.isPresent;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public GlobalResponse registerAdmin(UserRequest user) {

//        if(user.getRoleId() != null){
//            RoleEntity roleEntity = roleRepository.findById(user.getRoleId())
//                    .orElseThrow(()-> new GlobalException("Role "+MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
//        }

        //Request ra entity ma name same vaena vane yo method le issue falxa modelMapper bala le
        if (userRepository.existsByEmail(user.getEmail())) {

            throw new GlobalException("User with email " + user.getEmail() + " "
                    + MyConstants.ERR_MSG_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        RoleEntity roleEntity = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        UserEntity userEntity = UserEntity.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .gender(user.getGender())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(roleEntity)
                .build();

        userRepository.save(userEntity);

        return GlobalResponseBuilder.buildSuccessResponse("User registered successfully");
    }


    public GlobalResponse registerPatient(PatientRequest patient) {

        UserEntity userEntity = null;
        if (!isEmpty(patient.getUser()) && patient.getUser() != null) {
//            userEntity = modelMapper.map(patient.getUser(), UserEntity.class);

            if (userRepository.existsByEmail(patient.getUser().getEmail())) {
                throw new GlobalException("User with email " + patient.getUser().getEmail() + " "
                        + MyConstants.ERR_MSG_ALREADY_EXISTS, HttpStatus.CONFLICT);
            }

            RoleEntity roleEntity = roleRepository.findByName(Role.PATIENT)
                    .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            userEntity = UserEntity.builder()
                    .firstName(patient.getUser().getFirstName())
                    .lastName(patient.getUser().getLastName())
                    .email(patient.getUser().getEmail())
                    .password(passwordEncoder.encode(patient.getUser().getPassword()))
                    .gender(patient.getUser().getGender())
                    .phone(patient.getUser().getPhone())
                    .address(patient.getUser().getAddress())
                    .role(roleEntity)
                    .build();

            userRepository.save(userEntity);
        }
//        PatientEntity patientEntity = new PatientEntity();
//        patientEntity.setAge(patient.getAge());
//        patientEntity.setHeight(patient.getHeight());
//        patientEntity.setWeight(patient.getWeight());
//        patientEntity.setImageUrl(patient.getImageUrl());
//        patientEntity.setUser(userEntity);


        //USING BUILDER METHOD
        PatientEntity patientEntity = PatientEntity.builder()
                .age(patient.getAge())
                .height(patient.getHeight())
                .weight(patient.getWeight())
                .user(userEntity)
                .build();

        patientRepository.save(patientEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Patient registered successfully");
    }


    public GlobalResponse loginUser(LoginRequest loginRequest) {

//        UserEntity userEntity = userRepository.findByUsername(user.getEmail())
//                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
//
//        if(!passwordEncoder.matches(user.getPassword(), userEntity.getPassword())){
//            throw new GlobalException(MyConstants.ERR_MSG_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
//        }
        try {
            //this will check both existance and password matching
//            Authentication authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


            UserEntity userEntity = userRepository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
                throw new GlobalException(MyConstants.ERR_MSG_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
            }

//            String email = authentication.getName();
//
//            UserEntity userEntity = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            String token = jwtUtils.generateToken(userEntity.getEmail(), userEntity.getRole().getName(), userEntity.getId());

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .userId(userEntity.getId())
                    .email(userEntity.getEmail())
                    .role(userEntity.getRole().getName())
                    .build();

            return GlobalResponseBuilder.buildSuccessResponseWithData("Login successful", loginResponse);
        } catch (BadCredentialsException e) {
            throw new GlobalException(MyConstants.ERR_MSG_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    public GlobalResponse uploadUserImage(String userId, MultipartFile file) {

        try {
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            String uploadDir = "uploads/user/" + userId + "/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path filePath = Paths.get(uploadDir + fileName);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            userEntity.setImageUrl(uploadDir + fileName);
            userRepository.save(userEntity);

            return GlobalResponseBuilder.buildSuccessResponse("Image uploaded successfully");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
