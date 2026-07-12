package com.example.docmate.payload.response;

import com.example.docmate.enums.DoctorRequestStatus;
import com.example.docmate.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRequestResponse {
    private String id;

    // User Information
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String phone;
    private String address;
    private String province;

    // Doctor Information
    private String specialization;
    private Double experience;
    private String qualification;
    private String consultationFee;

    // Documents
    private String citizenshipFrontUrl;
    private String citizenshipBackUrl;
    private String doctorLicenseUrl;
    private String educationCertificateUrl;

    // Request Information
    private DoctorRequestStatus requestStatus;
    private String reviewedBy;
    private String rejectionReason;
}
