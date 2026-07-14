package com.example.docmate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.MedicalRecordEntity;
import com.example.docmate.entity.MedicationEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.TestReportEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.MedicalRecordRequest;
import com.example.docmate.payload.request.MedicationRequest;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.MedicalRecordResponse;
import com.example.docmate.payload.response.MedicationResponse;
import com.example.docmate.payload.response.PatientResponse;
import com.example.docmate.payload.response.TestReportResponse;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.MedicalRecordRepository;
import com.example.docmate.repository.MedicationRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.TestReportRepository;
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.MailService;
import com.example.docmate.service.MedicalRecordService;
import com.example.docmate.utils.CommonMethods;
import com.example.docmate.utils.MyConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MailService mailService;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicationRepository medicationRepository;
    private final TestReportRepository testReportRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final CommonMethods commonMethods;

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Override
    public GlobalResponse createMedicalRecord(MedicalRecordRequest medicalRecordRequest, MultipartFile[] testReports) {

        if (medicalRecordRequest.getAppointmentId() == null || medicalRecordRequest.getAppointmentId().isBlank()) {
            throw new GlobalException("Appointment id is required", HttpStatus.BAD_REQUEST);
        }

        if (medicalRecordRepository.existsByAppointmentId(medicalRecordRequest.getAppointmentId())) {
            throw new GlobalException("Medical record already exists for this appointment", HttpStatus.BAD_REQUEST);
        }

        AppointmentEntity appointmentEntity = appointmentRepository.findById(medicalRecordRequest.getAppointmentId())
                .orElseThrow(() -> new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        PatientEntity patientEntity = null;
        if (appointmentEntity != null && appointmentEntity.getPatient() != null) {
            patientEntity = appointmentEntity.getPatient();
        }

        DoctorEntity doctorEntity = null;
        if (appointmentEntity != null && appointmentEntity.getDoctor() != null) {
            doctorEntity = appointmentEntity.getDoctor();
        }

        MedicalRecordEntity medicalRecordEntity = MedicalRecordEntity.builder()
                .appointment(appointmentEntity)
                .patient(patientEntity)
                .doctor(doctorEntity)
                .diagnosis(medicalRecordRequest.getDiagnosis())
                .notes(medicalRecordRequest.getNotes())
                .build();

        medicalRecordRepository.save(medicalRecordEntity);

        handleMedication(medicalRecordRequest.getMedications(), medicalRecordEntity);

        if (testReports != null && testReports.length > 0) {
            handleTestReports(testReports, medicalRecordEntity);
        }

        if(patientEntity != null) {
            UserEntity userEntity = patientEntity.getUser();
            String subject = "Medical Record Created";

            String body = "<p>Dear " + userEntity.getFirstName() + ",</p>"
                    + "<p>Your medical record has been successfully created and is now available in your DocMate account.</p>"
                    + "<p>You can log in to view your diagnosis, prescriptions, and other medical details.</p>"
                    + "<p>Thank you for choosing DocMate.</p>"
                    + "<p>Regards,<br>The DocMate Team</p>";

            try {
                mailService.sendMail(userEntity.getEmail(), subject, body);
            } catch (Exception e) {
                log.error("Failed to send email to {}", userEntity.getEmail(), e);
            }
        }

        return GlobalResponseBuilder.buildSuccessResponse("Medical record created successfully");
    }

    private void handleMedication(List<MedicationRequest> medications, MedicalRecordEntity medicalRecordEntity) {

        if (!medications.isEmpty()) {
            List<MedicationEntity> medicationEntities = medications.stream()
                    .map(medicationRequest -> {
                        MedicationEntity medicationEntity = modelMapper.map(medicationRequest, MedicationEntity.class);
                        medicationEntity.setMedicalRecord(medicalRecordEntity);
                        medicationEntity.setPatient(medicalRecordEntity.getPatient());
                        medicationEntity.setStartDate(medicationRequest.getStartDate());
                        medicationEntity.setEndDate(medicationRequest.getEndDate());
                        return medicationEntity;
                    })
                    .toList();
            medicationRepository.saveAll(medicationEntities);
        }
    }

    private void handleTestReports(MultipartFile[] testReports, MedicalRecordEntity medicalRecordEntity) {

        List<TestReportEntity> existingTestReports = testReportRepository.findByMedicalRecordId(medicalRecordEntity.getId());

        List<TestReportEntity> newTestReportEntities = new ArrayList<>();

        for (MultipartFile testReport : testReports) {

            try {
                Map uploadResult = cloudinary.uploader().upload(
                        testReport.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "docmate/patient/testReport/" + medicalRecordEntity.getPatient().getId(),
                                "resource_type", "image"
                        )
                );

                String imageUrl = uploadResult.get("secure_url").toString();
                String imagePublicId = uploadResult.get("public_id").toString();

                TestReportEntity testReportEntity = TestReportEntity.builder()
                        .medicalRecord(medicalRecordEntity)
                        .reportUrl(imageUrl)
                        .imagePublicId(imagePublicId)
                        .build();

                newTestReportEntities.add(testReportEntity);

            } catch (IOException e) {
                throw new GlobalException("Test report upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        testReportRepository.saveAll(newTestReportEntities);

        if (!existingTestReports.isEmpty()) {
            List<String> existingPublicIds = existingTestReports.stream()
                    .map(TestReportEntity::getImagePublicId)
                    .filter(publicId -> publicId != null && !publicId.isBlank())
                    .toList();

            for (String publicId : existingPublicIds) {
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (Exception e) {
                    // Log the exception or handle it as needed
                    System.out.println("Failed to delete old test report: " + publicId);
                }
            }
            testReportRepository.deleteAll(existingTestReports);

        }

    }

    @Override
    public GlobalResponse getAllMedicalRecords(){
        String email = commonMethods.getAuthenticatedUserEmail();

        UserEntity userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException(
                        "User " + MyConstants.ERR_MSG_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));

        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new GlobalException(
                        "Patient " + MyConstants.ERR_MSG_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));

        List<MedicalRecordEntity> medicalRecords =
                medicalRecordRepository.findByPatientId(patientEntity.getId());

        List<String> medicalRecordIds = medicalRecords.stream()
                .map(MedicalRecordEntity::getId)
                .toList();

        Map<String, List<MedicationEntity>> medicationsMap = medicationRepository.findByMedicalRecordIdIn(medicalRecordIds)
                .stream()
                .collect(Collectors.groupingBy(medication -> medication.getMedicalRecord().getId()));

        Map<String, List<TestReportEntity>> testReportsMap = testReportRepository.findByMedicalRecordIdIn(medicalRecordIds)
                .stream()
                .collect(Collectors.groupingBy(testReport -> testReport.getMedicalRecord().getId()));

        List<MedicalRecordResponse> medicalRecordResponses = medicalRecords.stream()
                .map(medicalRecord -> {

                    MedicalRecordResponse response = modelMapper.map(medicalRecord, MedicalRecordResponse.class);
                    response.setMedicalRecordId(medicalRecord.getId());

                    response.setPatient(modelMapper.map(medicalRecord.getPatient(), PatientResponse.class));
                    response.setDoctor(modelMapper.map(medicalRecord.getDoctor(), DoctorResponse.class));

                    response.setMedications(medicationsMap.getOrDefault(medicalRecord.getId(), new ArrayList<>())
                            .stream()
                            .map(medication -> {
                                MedicationResponse medicationResponse =
                                        modelMapper.map(medication, MedicationResponse.class);
                                medicationResponse.setStartDate(medication.getStartDate());
                                medicationResponse.setEndDate(medication.getEndDate());

                                return medicationResponse;
                            })
                            .toList());

                    response.setTestReports(testReportsMap.getOrDefault(medicalRecord.getId(), new ArrayList<>())
                            .stream()
                            .map(testReport ->{
                                TestReportResponse testReportResponse =modelMapper.map(testReport, TestReportResponse.class);
                                testReportResponse.setTestReportId(testReport.getId());

                                return testReportResponse;
                            })
                            .toList());

                    return response;
                })
                .toList();

        return GlobalResponseBuilder.buildSuccessResponseWithData("Medical records retrieved successfully", medicalRecordResponses);

    }

    @Override
    public GlobalResponse getMedicalRecordByAppointmentId(String appointmentId){

        MedicalRecordEntity medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new GlobalException(
                        "Medical record " + MyConstants.ERR_MSG_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));

      MedicalRecordResponse response = mapMedicalRecordDetails(medicalRecord);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Medical record retrieved successfully", response);
    }

    @Override
    public GlobalResponse getMedicalRecordById(String medicalRecordId){

        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new GlobalException(
                        "Medical record " + MyConstants.ERR_MSG_NOT_FOUND,
                        HttpStatus.NOT_FOUND
                ));

        MedicalRecordResponse response = mapMedicalRecordDetails(medicalRecord);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Medical record retrieved successfully", response);

    }

    private MedicalRecordResponse mapMedicalRecordDetails(MedicalRecordEntity medicalRecord) {

        MedicalRecordResponse response = modelMapper.map(medicalRecord, MedicalRecordResponse.class);
        response.setMedicalRecordId(medicalRecord.getId());

        response.setPatient(modelMapper.map(medicalRecord.getPatient(), PatientResponse.class));
        response.setDoctor(modelMapper.map(medicalRecord.getDoctor(), DoctorResponse.class));

        List<MedicationEntity> medications = medicationRepository.findByMedicalRecordId(medicalRecord.getId());
        response.setMedications(medications.stream()
                .map(medication -> {
                    MedicationResponse medicationResponse =
                            modelMapper.map(medication, MedicationResponse.class);
                    medicationResponse.setStartDate(medication.getStartDate());
                    medicationResponse.setEndDate(medication.getEndDate());

                    return medicationResponse;
                })
                .toList());

        List<TestReportEntity> testReports = testReportRepository.findByMedicalRecordId(medicalRecord.getId());
        response.setTestReports(testReports.stream()
                .map(testReport -> {
                    TestReportResponse testReportResponse = modelMapper.map(testReport, TestReportResponse.class);
                    testReportResponse.setTestReportId(testReport.getId());

                    return testReportResponse;
                })
                .toList());

        return response;
    }
}
