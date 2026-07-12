package com.example.docmate.repository;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorRequestEntity;
import com.example.docmate.enums.DoctorRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DoctorRequestRepository extends JpaRepository<DoctorRequestEntity, Integer> {

    Page<DoctorRequestEntity> findByRequestStatus(DoctorRequestStatus requestStatus, Pageable pageable);
    Optional<DoctorRequestEntity> findById(String doctorId);
}
