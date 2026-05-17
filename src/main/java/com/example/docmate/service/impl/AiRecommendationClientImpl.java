package com.example.docmate.service.impl;

import com.example.docmate.payload.request.AiSymptomRequest;
import com.example.docmate.payload.response.AiPredictionResponse;
import com.example.docmate.service.AiRecommendationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiRecommendationClientImpl implements AiRecommendationClient {

    private final RestTemplate restTemplate;

    @Value("${docmate.ai.base-url}")
    private String aiBaseUrl;


//  This is the method that calls your Python AI model.
    @Override
    public AiPredictionResponse predictSpecialization(String symptoms) {

        if (symptoms == null || symptoms.trim().isEmpty()) {
            throw new IllegalArgumentException("Symptoms are required for AI prediction");
        }

        String url = aiBaseUrl + "/predict-specialization";

        AiSymptomRequest request = new AiSymptomRequest(symptoms);

        AiPredictionResponse response =
                restTemplate.postForObject(url, request, AiPredictionResponse.class);

        if (response == null || response.getPredictedSpecialization() == null) {
            throw new IllegalStateException("AI service did not return predicted specialization");
        }

        return response;
    }

    }
