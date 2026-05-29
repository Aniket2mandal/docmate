package com.example.docmate.payload.response;

import com.example.docmate.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private String medicalRecordId;

    private String appointmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus appointmentStatus;

    private PatientResponse patient;

    private DoctorResponse doctor;

    private String diagnosis;
    private String notes;

    private List<MedicationResponse> medications;

    private List<TestReportResponse> testReports;

    private LocalDateTime createdDateTime;
}
