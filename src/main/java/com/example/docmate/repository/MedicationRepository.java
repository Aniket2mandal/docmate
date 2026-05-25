package com.example.docmate.repository;

import com.example.docmate.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<MedicationEntity,String> {
}
