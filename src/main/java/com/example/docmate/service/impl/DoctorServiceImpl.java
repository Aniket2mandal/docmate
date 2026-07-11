package com.example.docmate.service.impl;


import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorDocumentsEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.enums.ScheduleAvailabilityStatus;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import com.example.docmate.payload.request.DoctorSearchRequest;
import com.example.docmate.payload.response.CloudinaryUploadResponse;
import com.example.docmate.payload.response.CommonPageResponse;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.DoctorScheduleResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.DoctorDocumentsRepository;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.DoctorScheduleRepository;
import com.example.docmate.repository.RoleRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.DoctorService;
import com.example.docmate.utils.CommonMethods;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final CommonMethods commonMethods;
    private final AppointmentRepository appointmentRepository;
    private final DoctorDocumentsRepository doctorDocumentsRepository;


    @Override
    public GlobalResponse createDoctor(DoctorRequest doctor,
                                       MultipartFile citizenshipFront,
                                       MultipartFile citizenshipBack,
                                       MultipartFile license,
                                       MultipartFile educationCertificate) {
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

        if (citizenshipFront == null || citizenshipFront.isEmpty()) {
            throw new GlobalException("Citizenship front image is required.", HttpStatus.BAD_REQUEST);
        }

        if (citizenshipBack == null || citizenshipBack.isEmpty()) {
            throw new GlobalException("Citizenship back image is required.", HttpStatus.BAD_REQUEST);
        }

        if (license == null || license.isEmpty()) {
            throw new GlobalException("Doctor license is required.", HttpStatus.BAD_REQUEST);
        }

        if (educationCertificate == null || educationCertificate.isEmpty()) {
            throw new GlobalException("Higher education certificate is required.", HttpStatus.BAD_REQUEST);
        }


        String citizenshipFrontPath= commonMethods
                .buildPath("doctor-document",doctorEntity.getId(), "CitizenshipFront");

        CloudinaryUploadResponse citizenshipFrontUrl =
                commonMethods.uploadDoctorDocument(citizenshipFront, citizenshipFrontPath, null);

        String citizenshipBackPath= commonMethods
                .buildPath("doctor-document",doctorEntity.getId(), "CitizenshipBack");

        CloudinaryUploadResponse citizenshipBackUrl =
                commonMethods.uploadDoctorDocument(citizenshipBack, citizenshipBackPath, null);

        String doctorLicensePath= commonMethods
                .buildPath("doctor-document",doctorEntity.getId(), "DoctorLicense");

        CloudinaryUploadResponse doctorLicenseUrl =
                commonMethods.uploadDoctorDocument(license, doctorLicensePath, null);

        String educationCertificatePath= commonMethods
                .buildPath("doctor-document",doctorEntity.getId(), "EducationCertificate");

        CloudinaryUploadResponse educationCertificateUrl =
                commonMethods.uploadDoctorDocument(educationCertificate, educationCertificatePath, null);


        DoctorDocumentsEntity documentsEntity = DoctorDocumentsEntity.builder()
                .doctor(doctorEntity)
                .citizenshipFront(citizenshipFrontUrl.getUrl())
                .citizenshipFrontPublicId(citizenshipFrontUrl.getPublicId())
                .citizenshipBack(citizenshipBackUrl.getUrl())
                .citizenshipBackPublicId(citizenshipBackUrl.getPublicId())
                .doctorLicense(doctorLicenseUrl.getUrl())
                .doctorLicensePublicId(doctorLicenseUrl.getPublicId())
                .educationCertificate(educationCertificateUrl.getUrl())
                .educationCertificatePublicId(educationCertificateUrl.getPublicId())
                .build();

        doctorDocumentsRepository.save(documentsEntity);


        return GlobalResponseBuilder.buildSuccessResponse("Doctor created successfully");
    }

    @Override
    public GlobalResponse getAllDoctor(Pageable pageable) {
//        Pageable pageable = PageRequest.of(page, size);

        Page<DoctorEntity> doctorEntityList = doctorRepository.findAllDoctors(UserStatus.ACTIVE, pageable);

        List<DoctorResponse> doctorResponseList = doctorEntityList.getContent().stream()
                .map(
//                doctor -> buildDoctorResponse(doctor)
//                you can write as below also
                        this::buildDoctorResponse
                ).toList();

        CommonPageResponse<DoctorResponse> response = new CommonPageResponse<>();
        response.setPaginationInfo(CommonMethods.getPaginationInfo(doctorEntityList));
        response.setData(doctorResponseList);


        return GlobalResponseBuilder.buildSuccessResponseWithData("All doctor fetched successfully", response);

    }

    @Override
    public GlobalResponse getDoctorById(String id) {
        DoctorEntity doctorEntity = doctorRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
//        DoctorResponse doctorResponse = modelMapper.map(doctorEntity, DoctorResponse.class);
//        doctorResponse.setDoctorId(doctorEntity.getId());
//        if (doctorEntity.getUser() != null) {
//            UserResponse userResponse = modelMapper.map(doctorEntity.getUser(), UserResponse.class);
//            if (doctorEntity.getUser().getRole() != null) {
//                RoleResponse roleResponse = modelMapper.map(doctorEntity.getUser().getRole(), RoleResponse.class);
//                userResponse.setRole(roleResponse.getName());
//            }
//            doctorResponse.setUser(userResponse);
//        }

        DoctorResponse doctorResponse = buildDoctorResponse(doctorEntity);
        return GlobalResponseBuilder.buildSuccessResponseWithData("Doctor fetched successfully", doctorResponse);
    }


    @Override
    public GlobalResponse updateDoctor(DoctorRequest doctorRequest, String doctorId,
                                       MultipartFile citizenshipFront,
                                       MultipartFile citizenshipBack,
                                       MultipartFile license,
                                       MultipartFile educationCertificate) {

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (doctorRequest.getUser() != null) {
            UserEntity userEntity = userRepository.findById(doctorEntity.getUserId())
                    .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
            modelMapper.map(doctorRequest.getUser(), userEntity);

            userRepository.save(userEntity);

            doctorEntity.setUser(userEntity);
        }
//        doctorEntity.setSpecialization(doctorRequest.getSpecialization());
//        doctorEntity.setExperience(doctorRequest.getExperience());
//        doctorEntity.setConsultation_fee(doctorRequest.getConsultation_fee());
//        doctorEntity.setQualification(doctorRequest.getQualification());

        modelMapper.map(doctorRequest, doctorEntity);
        doctorRepository.save(doctorEntity);

        DoctorDocumentsEntity documentsEntity = doctorDocumentsRepository.findByDoctorId(doctorId);

        if (citizenshipFront != null) {

            String citizenshipFrontPath= commonMethods
                    .buildPath("doctor-document",doctorEntity.getId(), "CitizenshipFront");

            CloudinaryUploadResponse citizenshipFrontUrl =
                    commonMethods.uploadDoctorDocument(citizenshipFront, citizenshipFrontPath, documentsEntity.getCitizenshipFrontPublicId());

            documentsEntity.setCitizenshipFront(citizenshipFrontUrl.getUrl());
            documentsEntity.setCitizenshipFrontPublicId(citizenshipFrontUrl.getPublicId());
        }

        if (citizenshipBack != null) {

            String citizenshipBackPath= commonMethods
                    .buildPath("doctor-document",doctorEntity.getId(), "CitizenshipBack");

            CloudinaryUploadResponse citizenshipBackUrl =
                    commonMethods.uploadDoctorDocument(citizenshipBack, citizenshipBackPath, documentsEntity.getCitizenshipBackPublicId());

            documentsEntity.setCitizenshipBack(citizenshipBackUrl.getUrl());
            documentsEntity.setCitizenshipBackPublicId(citizenshipBackUrl.getPublicId());
        }

        if (license != null) {

            String doctorLicensePath= commonMethods
                    .buildPath("doctor-document",doctorEntity.getId(), "DoctorLicense");

            CloudinaryUploadResponse doctorLicenseUrl =
                    commonMethods.uploadDoctorDocument(license, doctorLicensePath, documentsEntity.getDoctorLicensePublicId());

            documentsEntity.setDoctorLicense(doctorLicenseUrl.getUrl());
            documentsEntity.setDoctorLicensePublicId(doctorLicenseUrl.getPublicId());
        }

        if (educationCertificate != null) {

            String educationCertificatePath= commonMethods
                    .buildPath("doctor-document",doctorEntity.getId(), "EducationCertificate");

            CloudinaryUploadResponse educationCertificateUrl =
                    commonMethods.uploadDoctorDocument(educationCertificate, educationCertificatePath, documentsEntity.getEducationCertificatePublicId());

            documentsEntity.setEducationCertificate(educationCertificateUrl.getUrl());
            documentsEntity.setEducationCertificatePublicId(educationCertificateUrl.getPublicId());
        }

        doctorDocumentsRepository.save(documentsEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Doctor updated successfully");
    }

    @Override
    public GlobalResponse createDoctorSchedule(DoctorScheduleRequest scheduleRequest) {

        //if request is using id and your entity has both id and join column than modelMapper will
        // not work and you have to use builder

        String email = commonMethods.getAuthenticatedUserEmail();

        UserEntity userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (userEntity.getRole() == null || userEntity.getRole().getName() != Role.DOCTOR) {
            throw new GlobalException(MyConstants.ERR_MSG_UNAUTHORIZED, HttpStatus.FORBIDDEN);
        }

//        DoctorScheduleEntity doctorScheduleEntity=modelMapper.map(scheduleRequest,DoctorScheduleEntity.class);
        LocalDate startDate = scheduleRequest.getStartDate();
        LocalDate endDate = scheduleRequest.getEndDate();

        if (startDate.isBefore(LocalDate.now())) {
            throw new GlobalException("Start date must be in the future", HttpStatus.BAD_REQUEST);
        }

        if (scheduleRequest.getStartTime().isAfter(scheduleRequest.getEndTime())) {
            throw new GlobalException("Start time must be before end time", HttpStatus.BAD_REQUEST);
        }

        if (scheduleRequest.getStartTime().getMinute() != 0 && scheduleRequest.getStartTime().getMinute() != 30) {
            throw new GlobalException(
                    "Start time minute must be 00 or 30. Example: 12:00, 12:30",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (scheduleRequest.getEndTime().getMinute() != 0 && scheduleRequest.getEndTime().getMinute() != 30) {
            throw new GlobalException(
                    "End time minute must be 00 or 30. Example: 12:00, 12:30",
                    HttpStatus.BAD_REQUEST
            );
        }

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<DoctorScheduleEntity> doctorScheduleEntityList = new ArrayList<>();

        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {

            LocalTime slotStartTime = scheduleRequest.getStartTime();
            LocalTime slotEndTime = slotStartTime.plusMinutes(30);

            while (slotStartTime.isBefore(scheduleRequest.getEndTime())) {

                if (startDate.isEqual(LocalDate.now())) {
                    if (scheduleRequest.getStartTime().isBefore(LocalTime.now())) {
                        throw new GlobalException("Start time must be in the future", HttpStatus.BAD_REQUEST);
                    }
                }

                boolean scheduleAlreadyExists =
                        doctorScheduleRepository.existsOverlappingSchedule(
                                doctorEntity.getId(),
                                startDate,
                                slotStartTime,
                                slotEndTime
                        );

                if (scheduleAlreadyExists) {
                    throw new GlobalException(
                            "Schedule already exists for doctor on " + startDate,
                            HttpStatus.BAD_REQUEST
                    );
                }

                DoctorScheduleEntity doctorScheduleEntity = DoctorScheduleEntity.builder()
                        .availableDay(startDate.getDayOfWeek())
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(slotStartTime)
                        .endTime(slotEndTime)
                        .doctor(doctorEntity)
                        .build();

                slotStartTime = slotStartTime.plusMinutes(30);
                slotEndTime = slotEndTime.plusMinutes(30);

                doctorScheduleEntityList.add(doctorScheduleEntity);
            }

            startDate = startDate.plusDays(1);
        }

        doctorScheduleRepository.saveAll(doctorScheduleEntityList);
        return GlobalResponseBuilder.buildSuccessResponse("Doctor Schedule created");
    }

    @Override
    public GlobalResponse getAllSchedule(String doctorId) {
        List<DoctorScheduleEntity> doctorScheduleEntityList = doctorScheduleRepository.findByDoctorId(doctorId);
        List<DoctorScheduleResponse> doctorScheduleResponseList = doctorScheduleEntityList.stream()
                .map(schedule -> {
//                        modelMapper.map(schedule,DoctorScheduleResponse.class))
                    DoctorScheduleResponse doctorScheduleResponse = DoctorScheduleResponse.builder()
                            .id(schedule.getId())
                            .availableDay(schedule.getAvailableDay())
                            .available(schedule.getAvailable())
                            .startDate(schedule.getStartDate())
                            .endDate(schedule.getEndDate())
                            .startTime(schedule.getStartTime())
                            .endTime(schedule.getEndTime())
                            .build();

                    if (schedule.getAvailable() == ScheduleAvailabilityStatus.BOOKED || schedule.getAvailable() == ScheduleAvailabilityStatus.COMPLETED) {
                        AppointmentEntity appointmentEntity = appointmentRepository.findByDoctorScheduleId(schedule.getId())
                                .orElseThrow(() -> new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
                        if (appointmentEntity != null) {
                            doctorScheduleResponse.setAppointmentId(appointmentEntity.getId());
                        }
                    }
                    return doctorScheduleResponse;
                })
                .toList();
        return GlobalResponseBuilder.buildSuccessResponseWithData("Doctor schedule fetched succesfully", doctorScheduleResponseList);
    }

    @Override
    public GlobalResponse getAvailableSlots(String doctorId) {

        List<DoctorScheduleEntity> availableSlots = doctorScheduleRepository
                .findByDoctorIdAndAvailable(doctorId, ScheduleAvailabilityStatus.AVAILABLE);

//        Duplicated code

        List<DoctorScheduleResponse> availableSlotResponses = availableSlots.stream()
                .sorted(
                        Comparator.comparing(DoctorScheduleEntity::getStartDate)
                                .thenComparing(DoctorScheduleEntity::getStartTime)
                )
                .map(slot -> {
                    DoctorScheduleResponse doctorScheduleResponse = modelMapper.map(slot, DoctorScheduleResponse.class);
                    doctorScheduleResponse.setStartDate(slot.getStartDate());
                    doctorScheduleResponse.setEndDate(slot.getEndDate());
                    doctorScheduleResponse.setStartTime(slot.getStartTime());
                    doctorScheduleResponse.setEndTime(slot.getEndTime());

                    if (slot.getAvailable() == ScheduleAvailabilityStatus.BOOKED || slot.getAvailable() == ScheduleAvailabilityStatus.COMPLETED) {
                        AppointmentEntity appointmentEntity = appointmentRepository.findByDoctorScheduleId(slot.getId())
                                .orElseThrow(() -> new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));
                        if (appointmentEntity != null) {
                            doctorScheduleResponse.setAppointmentId(appointmentEntity.getId());
                        }

                    }

                    return doctorScheduleResponse;

                })
                .toList();

        return GlobalResponseBuilder.buildSuccessResponseWithData("Available slots fetched succesfully", availableSlotResponses);
    }

    @Override
    public GlobalResponse getDoctorDetails(String doctorId) {

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        DoctorResponse doctorResponse = modelMapper.map(doctorEntity, DoctorResponse.class);
        doctorResponse.setDoctorId(doctorEntity.getId());
        if (doctorEntity.getUser() != null) {
            UserResponse userResponse = modelMapper.map(doctorEntity.getUser(), UserResponse.class);
            doctorResponse.setUser(userResponse);
        }

        List<DoctorScheduleEntity> doctorScheduleEntity = doctorScheduleRepository.findByDoctorId(doctorId);

        List<DoctorScheduleResponse> doctorScheduleResponseList = doctorScheduleEntity.stream()
                .map(schedule -> {
                    DoctorScheduleResponse doctorScheduleResponse = modelMapper.map(schedule, DoctorScheduleResponse.class);
                    doctorScheduleResponse.setStartDate(schedule.getStartDate());
                    doctorScheduleResponse.setEndDate(schedule.getEndDate());
                    doctorScheduleResponse.setStartTime(schedule.getStartTime());
                    doctorScheduleResponse.setEndTime(schedule.getEndTime());

                    return doctorScheduleResponse;
                })
                .toList();
        doctorResponse.setSchedules(doctorScheduleResponseList);

        DoctorDocumentsEntity documentsEntity = doctorDocumentsRepository.findByDoctorId(doctorId);

        if(documentsEntity != null) {
            doctorResponse.setCitizenshipFrontUrl(documentsEntity.getCitizenshipFront());
            doctorResponse.setCitizenshipBackUrl(documentsEntity.getCitizenshipBack());
            doctorResponse.setDoctorLicenseUrl(documentsEntity.getDoctorLicense());
            doctorResponse.setEducationCertificateUrl(documentsEntity.getEducationCertificate());
        }

        return GlobalResponseBuilder.buildSuccessResponseWithData("Doctor Details", doctorResponse);
    }

    @Override
    public GlobalResponse deleteSchedule(String scheduleId) {
        DoctorScheduleEntity doctorScheduleEntity = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GlobalException("Doctor Schedule " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (doctorScheduleEntity.getAvailable() == ScheduleAvailabilityStatus.BOOKED || doctorScheduleEntity.getAvailable() == ScheduleAvailabilityStatus.COMPLETED) {
            throw new GlobalException("Cannot delete a booked schedule", HttpStatus.BAD_REQUEST);
        }

        doctorScheduleRepository.delete(doctorScheduleEntity);
        return GlobalResponseBuilder.buildSuccessResponse("Doctor Schedule deleted successfully");
    }

    @Override
    public GlobalResponse deleteDoctor(String doctorId) {

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<DoctorScheduleEntity> doctorScheduleEntities = doctorScheduleRepository.findByDoctorId(doctorId);

        for (DoctorScheduleEntity schedule : doctorScheduleEntities) {
            if (schedule.getAvailable() == ScheduleAvailabilityStatus.BOOKED) {
                throw new GlobalException("Cannot delete a doctor with booked schedules", HttpStatus.BAD_REQUEST);
            }
        }

        // Delete all schedules associated with the doctor
        doctorScheduleRepository.deleteAll(doctorScheduleEntities);

        // Delete the doctor
        doctorRepository.delete(doctorEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Doctor deleted successfully");
    }

    @Override
    public GlobalResponse searchDoctor(DoctorSearchRequest doctorRequest, Pageable pageable) {

        Page<DoctorEntity> doctorEntities =
                doctorRepository.findActiveDoctorsBySpecializationAndProvince(doctorRequest.getSpecialization(),
                        UserStatus.ACTIVE, doctorRequest.getProvince(), pageable);

        List<DoctorResponse> doctorResponseList = doctorEntities.getContent().stream()
                .map(
//                doctor -> buildDoctorResponse(doctor)
//                you can write as below also
                        this::buildDoctorResponse
                ).toList();

        CommonPageResponse<DoctorResponse> response = new CommonPageResponse<>();
        response.setPaginationInfo(CommonMethods.getPaginationInfo(doctorEntities));
        response.setData(doctorResponseList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Doctors", response);
    }



    @Override
   public GlobalResponse ApplyForDoctor(DoctorRequest doctor,
                                  MultipartFile citizenshipFront,MultipartFile citizenshipBack,
                                  MultipartFile license, MultipartFile educationCertificate){




        return null;
    }

    public DoctorResponse buildDoctorResponse(DoctorEntity doctor) {

        DoctorResponse doctorResponse = modelMapper.map(doctor, DoctorResponse.class);
        doctorResponse.setDoctorId(doctor.getId());
        if (doctor.getUser() != null) {
            UserResponse userResponse = modelMapper.map(doctor.getUser(), UserResponse.class);
            if (doctor.getUser().getRole() != null) {
                RoleResponse roleResponse = modelMapper.map(doctor.getUser().getRole(), RoleResponse.class);
                userResponse.setRole(roleResponse.getName());
            }
            doctorResponse.setUser(userResponse);
        }
        return doctorResponse;

    }


}
