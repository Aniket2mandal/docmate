package com.example.docmate.payload.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorRecommendationRequest {

    private String symptoms;

//    @JsonProperty("preferred_date")
//    @JsonAlias("preferredDate")
    private LocalDate preferredDate;

    // Optional. If patient manually selects specialization, we prioritize this.
    private String specialization;
}
