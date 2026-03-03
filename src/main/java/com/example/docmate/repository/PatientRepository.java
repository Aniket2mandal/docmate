package com.example.docmate.repository;

import com.example.docmate.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, String> {
    Optional<PatientEntity> findByUserId(String userId);
}
