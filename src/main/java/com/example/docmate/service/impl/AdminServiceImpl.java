package com.example.docmate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.docmate.entity.DoctorDocumentsEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorRequestEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.DoctorRequestStatus;
import com.example.docmate.enums.Role;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.exception.GlobalExceptionHandler;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.CloudinaryUploadResponse;
import com.example.docmate.payload.response.CommonPageResponse;
import com.example.docmate.payload.response.DoctorRequestResponse;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.DoctorDocumentsRepository;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.DoctorRequestRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AdminService;
import com.example.docmate.service.DoctorService;
import com.example.docmate.service.MailService;
import com.example.docmate.service.PatientService;
import com.example.docmate.utils.CommonMethods;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final MailService mailService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DoctorRequestRepository doctorRequestRepository;
    private final DoctorRepository doctorRepository;
    private final CommonMethods commonMethods;
    private final DoctorDocumentsRepository doctorDocumentsRepository;

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final PatientRepository patientRepository;

    public GlobalResponse createRole(RoleRequest role) {
        RoleEntity roleEntity = modelMapper.map(role, RoleEntity.class);
        roleRepository.save(roleEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Role is Created");
    }

    public GlobalResponse getAllRole() {
        List<RoleEntity> roleEntityList = roleRepository.findAll();
        List<RoleResponse> roleResponseList = roleEntityList.stream()
                .map(role -> modelMapper.map(role, RoleResponse.class))
                .toList();
        return GlobalResponseBuilder.buildSuccessResponseWithData("All role fetched", roleResponseList);
    }

    @Override
    public GlobalResponse changeStatus(UserRequest user, String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        userEntity.setStatus(user.getStatus());
        userRepository.save(userEntity);
        return GlobalResponseBuilder.buildSuccessResponse("User status changed");
    }

    @Override
    public GlobalResponse getDoctorRequests(Pageable pageable) {
        Page<DoctorRequestEntity> doctorRequestEntity = doctorRequestRepository.findByRequestStatus(DoctorRequestStatus.PENDING, pageable);

        List<DoctorRequestResponse> doctorRequestResponseList = doctorRequestEntity.stream()
                .map(doctorRequest -> modelMapper.map(doctorRequest, DoctorRequestResponse.class))
                .toList();

        CommonPageResponse<DoctorRequestResponse> response = new CommonPageResponse<>();
        response.setPaginationInfo(CommonMethods.getPaginationInfo(doctorRequestEntity));
        response.setData(doctorRequestResponseList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("All doctor requests fetched", response);
    }

    @Override
    public GlobalResponse getDoctorRequest(String doctorRequestId) {
        DoctorRequestEntity doctorRequestEntity = doctorRequestRepository.findById(doctorRequestId)
                .orElseThrow(() -> new GlobalException("Request " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        DoctorRequestResponse doctorRequestResponse = modelMapper.map(doctorRequestEntity, DoctorRequestResponse.class);
        doctorRequestResponse.setCitizenshipFrontUrl(doctorRequestEntity.getCitizenshipFront());
        doctorRequestResponse.setCitizenshipBackUrl(doctorRequestEntity.getCitizenshipBack());
        doctorRequestResponse.setDoctorLicenseUrl(doctorRequestEntity.getDoctorLicense());
        doctorRequestResponse.setEducationCertificateUrl(doctorRequestEntity.getEducationCertificate());

        return GlobalResponseBuilder.buildSuccessResponseWithData(" Doctor request fetched", doctorRequestResponse);
    }

    @Override
    public GlobalResponse approveDoctorRequest(String doctorRequestId) {

        DoctorRequestEntity doctorRequestEntity = doctorRequestRepository.findById(doctorRequestId)
                .orElseThrow(() -> new GlobalException("Request " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        doctorRequestEntity.setRequestStatus(DoctorRequestStatus.APPROVED);

        RoleEntity roleEntity = roleRepository.findByName(Role.DOCTOR)
                .orElseThrow(() -> new GlobalException("Role " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        UserEntity userEntity = UserEntity.builder()
                .firstName(doctorRequestEntity.getFirstName())
                .lastName(doctorRequestEntity.getLastName())
                .email(doctorRequestEntity.getEmail())
                .phone(doctorRequestEntity.getPhone())
                .password(doctorRequestEntity.getPassword())
                .gender(doctorRequestEntity.getGender())
                .address(doctorRequestEntity.getAddress())
                .province(doctorRequestEntity.getProvince())
                .role(roleEntity)
                .build();
        userRepository.save(userEntity);

        DoctorEntity doctorEntity = DoctorEntity.builder()
                .user(userEntity)
                .specialization(doctorRequestEntity.getSpecialization())
                .experience(doctorRequestEntity.getExperience())
                .qualification(doctorRequestEntity.getQualification())
                .consultation_fee(doctorRequestEntity.getConsultationFee())
                .build();
        doctorRepository.save(doctorEntity);


        String newCitizenshipFrontPath = commonMethods.buildPath(
                "doctor-document",
                doctorEntity.getId(),
                "CitizenshipFront");

        String newCitizenshipBackPath = commonMethods.buildPath(
                "doctor-document",
                doctorEntity.getId(),
                "CitizenshipBack");

        String newDoctorLicensePath = commonMethods.buildPath(
                "doctor-document",
                doctorEntity.getId(),
                "DoctorLicense");

        String newEducationCertificatePath = commonMethods.buildPath(
                "doctor-document",
                doctorEntity.getId(),
                "EducationCertificate");


        CloudinaryUploadResponse citizenshipFront =
                commonMethods.moveDocument(
                        doctorRequestEntity.getCitizenshipFront(),
                        doctorRequestEntity.getCitizenshipFrontPublicId(),
                        newCitizenshipFrontPath);

        CloudinaryUploadResponse citizenshipBack =
                commonMethods.moveDocument(
                        doctorRequestEntity.getCitizenshipBack(),
                        doctorRequestEntity.getCitizenshipBackPublicId(),
                        newCitizenshipBackPath);

        CloudinaryUploadResponse doctorLicense =
                commonMethods.moveDocument(
                        doctorRequestEntity.getDoctorLicense(),
                        doctorRequestEntity.getDoctorLicensePublicId(),
                        newDoctorLicensePath);

        CloudinaryUploadResponse educationCertificate =
                commonMethods.moveDocument(
                        doctorRequestEntity.getEducationCertificate(),
                        doctorRequestEntity.getEducationCertificatePublicId(),
                        newEducationCertificatePath);


        DoctorDocumentsEntity documentsEntity = DoctorDocumentsEntity.builder()
                .doctor(doctorEntity)
                .citizenshipFront(citizenshipFront.getUrl())
                .citizenshipFrontPublicId(citizenshipFront.getPublicId())
                .citizenshipBack(citizenshipBack.getUrl())
                .citizenshipBackPublicId(citizenshipBack.getPublicId())
                .doctorLicense(doctorLicense.getUrl())
                .doctorLicensePublicId(doctorLicense.getPublicId())
                .educationCertificate(educationCertificate.getUrl())
                .educationCertificatePublicId(educationCertificate.getPublicId())
                .build();

        doctorDocumentsRepository.save(documentsEntity);

        String reviewerEmail = commonMethods.getAuthenticatedUserEmail();

        doctorRequestEntity.setReviewedBy(reviewerEmail);
        doctorRequestRepository.save(doctorRequestEntity);

        commonMethods.deleteSubFolder("doctor-document-request", doctorRequestEntity.getId());

        String email=doctorRequestEntity.getEmail();
        String subject = "Docmate Application Approved";
        String body = "Dear " + doctorRequestEntity.getFirstName() + ",\n\n"
                + "Congratulations! Your application to join Docmate as a doctor has been approved.\n\n"
                + "You can now log in to your Docmate account and start managing your profile, "
                + "availability, and appointments.\n\n"
                + "Regards,\n"
                + "The Docmate Team";

        try {
            mailService.sendMail(email, subject, body);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email, e);
        }

        return GlobalResponseBuilder.buildSuccessResponse("Doctor approved successfully");
    }

    public GlobalResponse rejectDoctorRequest(String doctorRequestId, String reason) {

        DoctorRequestEntity doctorRequestEntity = doctorRequestRepository.findById(doctorRequestId)
                .orElseThrow(() -> new GlobalException("Request " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        doctorRequestEntity.setRequestStatus(DoctorRequestStatus.REJECTED);

        String reviewerEmail = commonMethods.getAuthenticatedUserEmail();

        doctorRequestEntity.setReviewedBy(reviewerEmail);
        doctorRequestEntity.setRejectionReason(reason);

        doctorRequestRepository.save(doctorRequestEntity);

        commonMethods.deleteFiles(doctorRequestEntity.getCitizenshipFrontPublicId());
        commonMethods.deleteFiles(doctorRequestEntity.getCitizenshipBackPublicId());
        commonMethods.deleteFiles(doctorRequestEntity.getDoctorLicensePublicId());
        commonMethods.deleteFiles(doctorRequestEntity.getEducationCertificatePublicId());

        commonMethods.deleteSubFolder("doctor-document-request", doctorRequestEntity.getId());

        String email = doctorRequestEntity.getEmail();
        String subject = "Docmate Application Update";
        String body = "Dear " + doctorRequestEntity.getFirstName() + ",\n\n"
                + "Thank you for applying to join Docmate as a doctor. "
                + "After review, we are unable to approve your request at this time.\n\n"
                + "Reason: " + reason + "\n\n"
                + "Regards,\n"
                + "The Docmate Team";

        try {
            mailService.sendMail(email, subject, body);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email, e);
        }

        return GlobalResponseBuilder.buildSuccessResponse("Doctor request rejected !");
    }

    @Override
    public GlobalResponse getAllUsers(Pageable pageable) {
        String authenticatedEmail = commonMethods.getAuthenticatedUserEmail();

        UserEntity authenticatedUser = userRepository.findByEmailAndStatus(authenticatedEmail, UserStatus.ACTIVE)
                 .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        boolean viewerIsSuperAdmin = authenticatedUser.getRole().getName().equals(Role.SUPER_ADMIN);

        Page<UserEntity> userEntityPage = userRepository.findAll(pageable);

        List<UserResponse> userResponsesList = userEntityPage.stream()
                .filter(user -> !authenticatedEmail.equals(user.getEmail())) // exclude self
                .filter(user -> {
                    Role role = user.getRole().getName();
                    if (viewerIsSuperAdmin) {
                        return true; // superadmin sees everyone else (already excluded self above)
                    }
                    // non-superadmin (i.e., ADMIN) viewers only see non-admin, non-superadmin users
                    return !role.equals(Role.ADMIN) && !role.equals(Role.SUPER_ADMIN);
                })
                .map(user -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setFirstName(user.getFirstName() + " " + user.getLastName());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setGender(user.getGender());
                    userResponse.setImageUrl(user.getImageUrl());
                    userResponse.setRole(user.getRole() != null ? user.getRole().getName() : null);

                    if (user.getRole() != null && user.getRole().getName().equals(Role.PATIENT)) {
                        patientRepository.findByUserId(user.getId())
                                .ifPresent(patientEntity -> userResponse.setIsPatientAvailable(true));
                    }

                    return userResponse;
                })
                .toList();

        CommonPageResponse<UserResponse> response = new CommonPageResponse<>();
        response.setPaginationInfo(CommonMethods.getPaginationInfo(userEntityPage));
        response.setData(userResponsesList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("User fetched Successfully", response);
    }

    @Override
    public GlobalResponse deleteUser(String userId){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if(userEntity.getRole().getName().equals(Role.PATIENT)) {
            Optional<PatientEntity> patientEntity=patientRepository.findByUserId(userEntity.getId());
            if(patientEntity.isPresent()) {
                String patientId=patientEntity.get().getId();
                patientService.deletePatient(patientId);
            }
        }
        if(userEntity.getRole().getName().equals(Role.DOCTOR)) {
            Optional<DoctorEntity> doctorEntity=doctorRepository.findByUserId(userEntity.getId());
            if(doctorEntity.isPresent()) {
                String doctorId=doctorEntity.get().getId();
                doctorService.deleteDoctor(doctorId);
            }
        }

        userRepository.delete(userEntity);
        return GlobalResponseBuilder.buildSuccessResponse("User deleted Successfully!");
    }
}
