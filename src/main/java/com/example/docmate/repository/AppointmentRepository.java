package com.example.docmate.repository;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {
    boolean existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
            String patientId, String doctorId, LocalDateTime appointmentDateTime, AppointmentStatus status);
}
