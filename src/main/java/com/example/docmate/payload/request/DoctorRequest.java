package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorRequest {
    private UserRequest user;
    private String specialization;
    private double experience;
    private String qualification;
    private String consultation_fee;

}
