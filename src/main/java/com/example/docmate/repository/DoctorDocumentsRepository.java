package com.example.docmate.repository;

import com.example.docmate.entity.DoctorDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorDocumentsRepository extends JpaRepository<DoctorDocumentsEntity, String> {
    DoctorDocumentsEntity findByDoctorId(String doctorId);
}
