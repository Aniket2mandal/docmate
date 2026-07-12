package com.example.docmate.payload.response;

import com.example.docmate.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String userId;
    private String email;
    private String name;
    private Role role;
    private String patientId;
    private String doctorId;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
