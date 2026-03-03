package com.example.docmate.payload.response;

import com.example.docmate.payload.request.UserRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorResponse {
    private UserRequest user;
    private String specialization;
    private double experience;
    private String qualification;
    private String consultation_fee;
}
