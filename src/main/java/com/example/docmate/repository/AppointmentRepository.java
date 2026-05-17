package com.example.docmate.repository;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {
    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
            String patientId, String doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status);


    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
            String doctorId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status);

    List<AppointmentEntity> findByPatientId(String patientId);

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.patientId = :patientId " +
            "AND a.status = :status " +
            "AND (a.appointmentDate > :nowDate " +
            "OR (a.appointmentDate = :nowDate AND a.appointmentTime > :nowTime)) " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<AppointmentEntity> findUpcomingAppointmentsByPatientId(
            String patientId,
            LocalDate nowDate,
            LocalTime nowTime,
            AppointmentStatus status
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.status = :status " +
            "AND (a.appointmentDate > :nowDate " +
            "OR (a.appointmentDate = :nowDate AND a.appointmentTime > :nowTime)) " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<AppointmentEntity> findUpcomingAppointmentsByDoctorId(
            String doctorId,
            LocalDate nowDate,
            LocalTime nowTime,
            AppointmentStatus status
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.patientId = :patientId " +
            "AND a.status = :status " +
            "AND (a.appointmentDate < :nowDate " +
            "OR (a.appointmentDate = :nowDate AND a.appointmentTime < :nowTime)) " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<AppointmentEntity> findPreviousAppointmentsByPatientId(
            String patientId,
            LocalDate nowDate,
            LocalTime nowTime,
            AppointmentStatus status
    );

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.status = :status " +
            "AND (a.appointmentDate < :nowDate " +
            "OR (a.appointmentDate = :nowDate AND a.appointmentTime < :nowTime)) " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<AppointmentEntity> findPreviousAppointmentsByDoctorId(
            String doctorId,
            LocalDate nowDate,
            LocalTime nowTime,
            AppointmentStatus status
    );


    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.status = :status " +
            "AND (a.appointmentDate < :nowDate " +
            "OR (a.appointmentDate = :nowDate AND a.appointmentTime < :nowTime)) " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<AppointmentEntity> findPreviousAppointments(
            LocalDate nowDate,
            LocalTime nowTime,
            AppointmentStatus status
    );


}
