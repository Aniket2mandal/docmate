package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorRequest {

    @Valid
    @NotNull(message = "User details are required")
    private UserRequest user;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Experience is required")
    @DecimalMin(value = "0.0", message = "Experience cannot be negative")
    private Double experience;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "Consultation fee is required")
    private String consultation_fee;
}