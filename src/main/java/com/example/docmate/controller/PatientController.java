package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.RatingRequest;
import com.example.docmate.service.DoctorService;
import com.example.docmate.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/rate-doctor")
    public ResponseEntity<GlobalResponse> rateDoctor(@RequestBody RatingRequest ratingRequest) {
        return ResponseEntity.ok(patientService.rateDoctor(ratingRequest));
    }

}
