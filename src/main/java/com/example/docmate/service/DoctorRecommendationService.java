package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRecommendationRequest;
import com.example.docmate.payload.response.DoctorResponse;

import java.util.List;

public interface DoctorRecommendationService {
    GlobalResponse recommendDoctors(DoctorRecommendationRequest request);
}
