package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-admin")
    public ResponseEntity<GlobalResponse> registerAdmin(@RequestBody UserRequest user) {
         return ResponseEntity.ok(authService.registerAdmin(user));
     }

     @PostMapping("/register-patient")
    public ResponseEntity<GlobalResponse> registerPatient(@RequestBody PatientRequest patient,@RequestParam String imageUrl ) {
        patient.setImageUrl(imageUrl);
        return ResponseEntity.ok(authService.registerPatient(patient));
     }

     @PostMapping("/login-user")
    public ResponseEntity<GlobalResponse> loginUser(@RequestBody UserRequest user) {

     }
}
