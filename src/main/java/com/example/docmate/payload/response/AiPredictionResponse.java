package com.example.docmate.payload.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiPredictionResponse {

    @JsonProperty("predicted_specialization")
    @JsonAlias("predictedSpecialization")
    private String predictedSpecialization;
    private Double confidence;
    @JsonProperty("top_specializations")
    @JsonAlias("topSpecializations")
    private List<String> topSpecializations;
}
