package com.example.docmate.repository;

import com.example.docmate.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, String> {

    Optional<PatientEntity> findByUserId(String userId);

    @Query("SELECT p FROM PatientEntity p " +
            "JOIN FETCH p.user u " +
            "WHERE u.status = 'ACTIVE'")
    Page<PatientEntity> findAllActivePatients(Pageable pageable);
}
