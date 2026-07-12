package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {

    @Valid
    @NotNull(message = "User details are required")
    private UserRequest user;
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 120, message = "Age must be realistic")
    private int age;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    @Max(value = 500, message = "Weight seems unrealistic")
    private double weight;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be greater than 0")
    @Max(value = 300, message = "Height seems unrealistic")
    private double height;
    private String imageUrl;
}
