package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.RatingRequest;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    GlobalResponse getAllPatient(Pageable pageable);
    GlobalResponse deletePatient(String patientId);
    GlobalResponse rateDoctor(RatingRequest ratingRequest);

}
