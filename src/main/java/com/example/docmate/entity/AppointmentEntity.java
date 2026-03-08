package com.example.docmate.entity;

import com.example.docmate.enums.AppointmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_appointment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEntity extends BaseEntity {

    @Column(name="appointment_date_time")
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name="appointment_status")
    private AppointmentStatus appointmentStatus;

    @Column(name="reason_for_visit")
    private String reasonForVisit;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;

    @Column(name="doctor_id", insertable = false, updatable = false)
    private String doctorId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private PatientEntity patient;

    @Column(name="patient_id", insertable = false, updatable = false)
    private String patientId;
}
