package com.example.docmate.controller;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    @PostMapping("/create")
    public ResponseEntity<GlobalResponse> createDoctor(@RequestBody DoctorRequest doctor){
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }
}
