package com.example.docmate.entity;

import com.example.docmate.enums.FrequencyEnum;
import com.example.docmate.enums.MedicationStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_medication")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicationEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name="medical_record_id",nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name="medical_record_id",insertable = false,updatable = false)
    private String medicalRecordId;

    @ManyToOne
    @JoinColumn(name="patient_id",nullable = false)
    private PatientEntity patient;

    @Column(name="patient_id",insertable = false,updatable = false)
    private String patientId;

    @Column(name="medicine_name")
    private String medicineName;

    @Column(name="dosage")
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name="frequency")
    private FrequencyEnum frequency;

    @Column(name="time_schedule")
    private String timeSchedule;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name="instruction",columnDefinition = "TEXT")
    private String instruction;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private MedicationStatusEnum status;


}
