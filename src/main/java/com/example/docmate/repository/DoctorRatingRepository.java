package com.example.docmate.repository;

import com.example.docmate.entity.DoctorRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRatingRepository extends JpaRepository<DoctorRatingEntity, String> {

    List<DoctorRatingEntity> findByDoctorId(String doctorId);
    DoctorRatingEntity findByDoctorIdAndPatientId(String doctorId, String patientId);
}
