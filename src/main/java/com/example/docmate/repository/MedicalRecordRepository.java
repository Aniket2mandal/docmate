package com.example.docmate.repository;

import com.example.docmate.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, String> {
    boolean existsByAppointmentId(String appointmentId);
    List<MedicalRecordEntity> findByPatientId(String appointmentId);
    Optional<MedicalRecordEntity> findByAppointmentId(String appointmentId);
}
