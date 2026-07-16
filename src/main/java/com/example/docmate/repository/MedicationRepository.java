package com.example.docmate.repository;

import com.example.docmate.entity.MedicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<MedicationEntity,String> {
    List<MedicationEntity> findByMedicalRecordIdIn(List<String> medicationIds);
    List<MedicationEntity> findByMedicalRecordId(String medicalRecordId);
    Page<MedicationEntity> findByPatientId(String patientId, Pageable pageable);
}
