package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.LoginRequest;
import com.example.docmate.payload.request.PatientRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.payload.response.LoginResponse;
import com.example.docmate.service.AuthService;
import com.example.docmate.service.RefreshTokenService;
import com.example.docmate.utils.CommonMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final CommonMethods commonMethods;
    private final RefreshTokenService refreshTokenService;

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

    // ✅ Client calls this when access token expires
    @PostMapping("/refresh")
    public ResponseEntity<GlobalResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(  refreshTokenService.rotateAccessToken(refreshToken));
    }

    @GetMapping("/user-profile")
    public ResponseEntity<GlobalResponse> getUserProfile() {
        return ResponseEntity.ok(authService.getUserProfile());
     }

     @PostMapping("/logout")
    public ResponseEntity<GlobalResponse> logoutUser() {
        // Implement logout logic if needed (e.g., invalidate JWT token)
         String userEmail=commonMethods.getAuthenticatedUserEmail();
        return ResponseEntity.ok(authService.logoutUser(userEmail));
     }
}
