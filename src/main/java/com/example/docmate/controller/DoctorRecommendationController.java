package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRecommendationRequest;
import com.example.docmate.service.DoctorRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class DoctorRecommendationController {

    private final DoctorRecommendationService doctorRecommendationService;

    @PostMapping("/doctors-by-symptoms")
    public ResponseEntity<GlobalResponse> recommendDoctors(
            @RequestBody DoctorRecommendationRequest request
    ) {
        return ResponseEntity.ok(doctorRecommendationService.recommendDoctors(request));
    }
}
