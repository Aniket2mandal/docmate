package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.ForgotPasswordRequest;
import com.example.docmate.payload.request.LoginRequest;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    GlobalResponse registerAdmin(UserRequest user);
    GlobalResponse registerPatient(PatientRequest patient);
    GlobalResponse loginUser(LoginRequest loginRequest);
    GlobalResponse uploadUserImage(String userId, MultipartFile file);
    GlobalResponse getUserProfile();
    GlobalResponse logoutUser(String userEmail);
    GlobalResponse sendOtp(String email);
    GlobalResponse verifyOtp(ForgotPasswordRequest request);
    GlobalResponse updatePassword(ForgotPasswordRequest request);
}
