package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.RatingRequest;

public interface PatientService {
    GlobalResponse getAllPatient();
    GlobalResponse deletePatient(String patientId);
    GlobalResponse rateDoctor(RatingRequest ratingRequest);

}
