package com.example.docmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_test_reports")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestReportEntity extends BaseEntity {

    @Column(name="report_url")
    private String reportUrl;

    @Column(name="image_public_id")
    private String imagePublicId;

    @ManyToOne
    @JoinColumn(name="medical_record_id",nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name="medical_record_id",insertable = false,updatable = false)
    private String medicalRecordId;

}
