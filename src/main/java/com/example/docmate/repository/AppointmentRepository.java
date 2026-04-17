package com.example.docmate.repository;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {
    boolean existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
            String patientId, String doctorId, LocalDateTime appointmentDateTime, AppointmentStatus status);
    List<AppointmentEntity> findByPatientId(String patientId);

    List<AppointmentEntity> findByPatientIdAndAppointmentDateTimeAfterAndStatus(
            String patientId, LocalDateTime appointmentDateTime, AppointmentStatus status);

    List<AppointmentEntity> findByPatientIdAndAppointmentDateTimeBeforeAndStatus(
            String patientId, LocalDateTime appointmentDateTime, AppointmentStatus status);

    List<AppointmentEntity> findByDoctorIdAndAppointmentDateTimeAfterAndStatus(
            String doctorId, LocalDateTime appointmentDateTime, AppointmentStatus status);

    List<AppointmentEntity> findByDoctorIdAndAppointmentDateTimeBeforeAndStatus(
            String doctorId, LocalDateTime appointmentDateTime, AppointmentStatus status);

}
