package com.example.docmate.repository;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {
    Optional<DoctorEntity> findByUserId(String userId);

    @Query("SELECT d " +
            "FROM DoctorEntity d " +
            "JOIN FETCH d.user u " +
            "WHERE LOWER(TRIM(d.specialization)) IN ?1 " +
            "AND u.status = :status")
    List<DoctorEntity> findActiveDoctorsBySpecializations(
            List<String> specializations,
            UserStatus status
    );

    @Query("SELECT d " +
            "FROM DoctorEntity d " +
            "JOIN FETCH d.user u " +
            "WHERE u.status = :status")
    Page<DoctorEntity> findAllDoctors(UserStatus status, Pageable pageable);
}
