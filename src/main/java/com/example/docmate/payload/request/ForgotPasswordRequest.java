package com.example.docmate.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest {
private String email;
private String otp;
private Boolean isVerified;

@NotBlank(message = "Password is required")
@Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
private String newPassword;
}
