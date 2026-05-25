package com.example.docmate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.MedicalRecordEntity;
import com.example.docmate.entity.MedicationEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.TestReportEntity;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.MedicalRecordRequest;
import com.example.docmate.payload.request.MedicationRequest;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.MedicalRecordRepository;
import com.example.docmate.repository.MedicationRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.repository.TestReportRepository;
import com.example.docmate.service.MedicalRecordService;
import com.example.docmate.utils.MyConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicationRepository medicationRepository;
    private final TestReportRepository testReportRepository;
    private final Cloudinary cloudinary;

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

}
