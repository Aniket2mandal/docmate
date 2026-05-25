package com.example.docmate.repository;

import com.example.docmate.entity.TestReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestReportRepository extends JpaRepository<TestReportEntity,String> {
    List<TestReportEntity> findByMedicalRecordId(String medicalRecordId);
}
