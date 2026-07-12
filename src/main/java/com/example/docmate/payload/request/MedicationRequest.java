package com.example.docmate.payload.request;

import com.example.docmate.enums.FrequencyEnum;
import com.example.docmate.enums.MedicationStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationRequest {
    private String medicalRecordId;

    private String patientId;

    private String medicineName;

    private String dosage;

    private FrequencyEnum frequency;

    private String timeSchedule;

    private LocalDate startDate;

    private LocalDate endDate;

    private String instruction;

    private MedicationStatusEnum status;
}
