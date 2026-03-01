package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.LoginRequest;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<GlobalResponse> registerPatient(@RequestBody PatientRequest patient) {
        return ResponseEntity.ok(authService.registerPatient(patient));
     }

     @PostMapping(value="/upload-user-image/{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<GlobalResponse> uploadUserImage(@PathVariable("userId") String userId,
                                                          @RequestPart(value = "file", required = false) MultipartFile file){
        return ResponseEntity.ok(authService.uploadUserImage(userId, file));
     }

     @PostMapping("/login-user")
    public ResponseEntity<GlobalResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.loginUser(loginRequest));
     }
}
