package com.example.docmate.repository;

import com.example.docmate.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, String> {
    boolean existsByAppointmentId(String appointmentId);
}
