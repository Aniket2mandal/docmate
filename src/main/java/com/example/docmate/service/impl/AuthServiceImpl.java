package com.example.docmate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.PasswordResetOtpEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.RefreshTokenEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.ForgotPasswordRequest;
import com.example.docmate.payload.request.LoginRequest;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.CloudinaryUploadResponse;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.LoginResponse;
import com.example.docmate.payload.response.PatientResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.PasswordResetOtpRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.RefreshTokenRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AuthService;
import com.example.docmate.service.MailService;
import com.example.docmate.service.RefreshTokenService;
import com.example.docmate.utils.CommonMethods;
import com.example.docmate.utils.JwtUtils;
import com.example.docmate.utils.MyConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.annotations.UuidGenerator.Style.RANDOM;
import static org.springframework.boot.autoconfigure.container.ContainerImageMetadata.isPresent;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MailService mailService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DoctorRepository doctorRepository;
    private final RefreshTokenService refreshTokenService;
    private final Cloudinary cloudinary;
    private final CommonMethods commonMethods;

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final PasswordResetOtpRepository passwordResetOtpRepository;

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

            if(userRepository.existsByPhone(patient.getUser().getPhone())){
                throw new GlobalException("User with phone " + MyConstants.ERR_MSG_ALREADY_EXISTS, HttpStatus.CONFLICT);
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
                    .province(patient.getUser().getProvince())
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

            UserEntity userEntity = userRepository.findByEmailAndStatus(loginRequest.getUsername(), UserStatus.ACTIVE)
                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getPassword())) {
                throw new GlobalException(MyConstants.ERR_MSG_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
            }

//            String email = authentication.getName();
//
//            UserEntity userEntity = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            String accessToken = jwtUtils.generateAccessToken(userEntity.getEmail(), userEntity.getRole().getName(), userEntity.getId());
            String refreshToken = jwtUtils.generateRefreshToken(userEntity.getEmail());

            refreshTokenService.createRefreshToken(userEntity.getEmail(), refreshToken);

            String patientId = null;
            String doctorId = null;

            Role role = userEntity.getRole().getName();

            if (role == Role.PATIENT) {
                patientId = patientRepository.findByUserId(userEntity.getId())
                        .map(PatientEntity::getId)
                        .orElse(null);
            }

            if (role == Role.DOCTOR) {
                doctorId = doctorRepository.findByUserId(userEntity.getId())
                        .map(DoctorEntity::getId)
                        .orElse(null);
            }

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(userEntity.getId())
                    .name(userEntity.getFirstName() + " " + userEntity.getLastName())
                    .patientId(patientId)
                    .doctorId(doctorId)
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

//            String uploadDir = "uploads/user/" + userId + "/";
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//            Path filePath = Paths.get(uploadDir + fileName);
//
//            Files.createDirectories(filePath.getParent());
//            Files.write(filePath, file.getBytes());
//
//            userEntity.setImageUrl(uploadDir + fileName);

            String oldImagePublicId = userEntity.getImagePublicId();

//            if (file == null || file.isEmpty()) {
//                throw new GlobalException("Please select an image", HttpStatus.BAD_REQUEST);
//            }
//
//            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
//                throw new GlobalException("Only image files are allowed", HttpStatus.BAD_REQUEST);
//            }
//
//            Map uploadResult = cloudinary.uploader().upload(
//                    file.getBytes(),
//                    ObjectUtils.asMap(
//                            "folder", "docmate/users/" + userId,
//                            "resource_type", "image"
//                    )
//            );
//
//            String imageUrl = uploadResult.get("secure_url").toString();
//            String imagePublicId = uploadResult.get("public_id").toString();

//            String path = "docmate/users/" + userId;

            String path = commonMethods.buildPath("users", userId, null);

            CloudinaryUploadResponse urlResponse = commonMethods.uploadDoctorDocument(file, path, oldImagePublicId);

            userEntity.setImageUrl(urlResponse.getUrl());
            userEntity.setImagePublicId(urlResponse.getPublicId());

            userRepository.save(userEntity);

            if (oldImagePublicId != null && !oldImagePublicId.isBlank()) {
                cloudinary.uploader().destroy(oldImagePublicId, ObjectUtils.emptyMap());
            }


            return GlobalResponseBuilder.buildSuccessResponse("Image uploaded successfully");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public GlobalResponse getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(" User is not authenticated ", HttpStatus.UNAUTHORIZED);
        }
        //getName() returns the "principal" (the main identifier), which YOU set as the email when creating the JWT token! so it returns email
        String email = authentication.getName();

        Role role = authentication.getAuthorities().stream().findFirst().get().getAuthority().equals("ROLE_ADMIN") ? Role.ADMIN :
                authentication.getAuthorities().stream().findFirst().get().getAuthority().equals("ROLE_DOCTOR") ? Role.DOCTOR : Role.PATIENT;

        UserEntity userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        if(userEntity.getRole() != null){
            userResponse.setRole(role);
        }

        if (role.equals(Role.PATIENT)) {
            PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                    .orElseThrow(() -> new GlobalException("Patient " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
            if (patientEntity != null) {
                PatientResponse patientResponse = modelMapper.map(patientEntity, PatientResponse.class);
                userResponse.setPatientCore(patientResponse);
                userResponse.setRole(role);
            }
        }

        if (role.equals(Role.DOCTOR)) {
            DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                    .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (doctorEntity != null) {
                userResponse.setDoctorCore(modelMapper.map(doctorEntity, DoctorResponse.class));
                userResponse.setRole(role);
            }
        }

        return GlobalResponseBuilder.buildSuccessResponseWithData("User profile fetched successfully", userResponse);
    }


    public GlobalResponse logoutUser(String userEmail) {

        UserEntity user = userRepository.findByEmailAndStatus(userEmail, UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User "+ MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        refreshTokenRepository.deleteByUserId(user.getId());

        return GlobalResponseBuilder.buildSuccessResponse("User logged out successfully");
    }

    @Override
    public GlobalResponse sendOtp(ForgotPasswordRequest request) {
        UserEntity user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        String otp = CommonMethods.generateOtp();

        String subject = "Reset Password OTP";

        String body = "<p>Dear," + user.getFirstName() + "</p>"
                + "<p>Your One-Time Password (OTP) for resetting your password is:</p>"
                + "<p><b style='font-size:20px;'>" + otp + "</b></p>"
                + "<p>This OTP is valid for 1 minute. Please do not share it with anyone.</p>"
                + "<p>If you did not request a password reset, you can safely ignore this email.</p>"
                + "<p>Regards,<br>The DocMate Team</p>";

        try {
            mailService.sendMail(request.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to send email to {}", request.getEmail(), e);
            throw new GlobalException("Failed to send OTP email.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        PasswordResetOtpEntity passwordResetOtpEntity = new PasswordResetOtpEntity();
        passwordResetOtpEntity.setOtp(otp);
        passwordResetOtpEntity.setUserId(user.getId());
        passwordResetOtpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(1));
        passwordResetOtpRepository.save(passwordResetOtpEntity);

        return GlobalResponseBuilder.buildSuccessResponse("OTP sent successfully");

    }

    @Override
    public GlobalResponse verifyOtp(ForgotPasswordRequest request){
        UserEntity user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        PasswordResetOtpEntity passwordResetOtpEntity = passwordResetOtpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GlobalException("otp " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!request.getOtp().equals(passwordResetOtpEntity.getOtp())) {
            throw new GlobalException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        if (passwordResetOtpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new GlobalException("OTP has expired", HttpStatus.BAD_REQUEST);
        }

        passwordResetOtpEntity.setIsVerified(true);
        passwordResetOtpRepository.save(passwordResetOtpEntity);

        return GlobalResponseBuilder.buildSuccessResponse("OTP verified!");
    }

    @Override
    public GlobalResponse updatePassword(ForgotPasswordRequest request){
        UserEntity user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        PasswordResetOtpEntity passwordResetOtpEntity = passwordResetOtpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new GlobalException("otp " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (passwordResetOtpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new GlobalException("OTP has expired", HttpStatus.BAD_REQUEST);
        }

        if(!passwordResetOtpEntity.getIsVerified()){
            throw new GlobalException("OTP is not verified", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new GlobalException(MyConstants.ERR_MSG_NEW_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetOtpRepository.delete(passwordResetOtpEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Password Changed!");
    }
}
