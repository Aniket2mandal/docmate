package com.example.docmate.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentResponse {
    private String appointmentId;
    private DoctorResponse doctor;
    private PatientResponse patient;
    private String appointmentDateTime;
    private String status;
    private String reasonForVisit;

}
