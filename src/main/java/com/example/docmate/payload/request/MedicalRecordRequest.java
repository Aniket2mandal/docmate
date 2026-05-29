package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequest {
    private String appointmentId;
//    private Long patientId;
//    private Long doctorId;

    private String diagnosis;
//    private List<String> testReports;
    private String notes;
    private List<MedicationRequest> medications;

}
