package com.example.docmate.entity;

import com.example.docmate.enums.DoctorRequestStatus;
import com.example.docmate.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_doctor_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestEntity extends BaseEntity {

    // User Information
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "province")
    private String province;


    // Doctor Information
    @Column(name = "specialization")
    private String specialization;

    @Column(name = "experience")
    private double experience;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "consultation_fee")
    private String consultationFee;


    // Documents
    @Column(name = "citizenship_front")
    private String citizenshipFront;

    @Column(name = "citizenship_front_public_id")
    private String citizenshipFrontPublicId;

    @Column(name = "citizenship_back")
    private String citizenshipBack;

    @Column(name = "citizenship_back_public_id")
    private String citizenshipBackPublicId;

    @Column(name = "doctor_license")
    private String doctorLicense;

    @Column(name = "doctor_license_public_id")
    private String doctorLicensePublicId;

    @Column(name = "education_certificate")
    private String educationCertificate;

    @Column(name = "education_certificate_public_id")
    private String educationCertificatePublicId;

    // Request
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    @Builder.Default
    private DoctorRequestStatus requestStatus = DoctorRequestStatus.PENDING;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;
}
