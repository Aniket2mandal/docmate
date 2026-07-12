package com.example.docmate.payload.response;

import com.example.docmate.payload.request.UserRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorResponse {
    private String doctorId;
    private UserResponse user;
    private String specialization;
    private double experience;
    private String qualification;
    private String consultation_fee;
    private double rating;
    private int ratingCount;

    private List<DoctorScheduleResponse> schedules;

//    for recommendation scoring
    private double ratingScore;

    private double experienceScore;

    private double specializationScore;

    private double availabilityScore;

    private double finalScore;

    private String aiPredictedSpecialization;

    private Double aiConfidence;

    //DOCTOR DOCUMENTS
    private String citizenshipFrontUrl;
    private String citizenshipBackUrl;
    private String doctorLicenseUrl;
    private String educationCertificateUrl;
}
