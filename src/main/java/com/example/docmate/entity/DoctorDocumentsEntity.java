package com.example.docmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_doctor_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDocumentsEntity extends BaseEntity {

    @Column(name = "citizenship_front", nullable = false)
    private String citizenshipFront;

    @Column(name = "citizenship_front_public_id", nullable = false)
    private String citizenshipFrontPublicId;


    @Column(name = "citizenship_back", nullable = false)
    private String citizenshipBack;

    @Column(name = "citizenship_back_public_id", nullable = false)
    private String citizenshipBackPublicId;


    @Column(name = "doctor_license", nullable = false)
    private String doctorLicense;

    @Column(name = "doctor_license_public_id", nullable = false)
    private String doctorLicensePublicId;


    @Column(name = "education_certificate", nullable = false)
    private String educationCertificate;

    @Column(name = "education_certificate_public_id", nullable = false)
    private String educationCertificatePublicId;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;


    @Column(name = "doctor_id", insertable = false, updatable = false)
    private String doctorId;

}