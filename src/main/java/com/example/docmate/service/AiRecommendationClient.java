package com.example.docmate.service;

import com.example.docmate.payload.response.AiPredictionResponse;

public interface AiRecommendationClient {
    AiPredictionResponse predictSpecialization(String symptoms);
}
