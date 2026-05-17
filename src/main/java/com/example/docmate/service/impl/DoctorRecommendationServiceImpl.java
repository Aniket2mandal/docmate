package com.example.docmate.service.impl;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.DoctorRecommendationRequest;
import com.example.docmate.payload.response.AiPredictionResponse;
import com.example.docmate.payload.response.DoctorResponse;
import com.example.docmate.payload.response.DoctorScheduleResponse;
import com.example.docmate.payload.response.RoleResponse;
import com.example.docmate.payload.response.UserResponse;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.DoctorScheduleRepository;
import com.example.docmate.service.DoctorRecommendationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorRecommendationServiceImpl implements DoctorRecommendationService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    @Value("${docmate.ai.low-confidence-limit:0.60}")
    private double lowConfidenceLimit;

    private static final int SEARCH_DAYS = 7;

    private final AiRecommendationClientImpl aiRecommendationClient;
    private final ModelMapper modelMapper;

    @Override
    public GlobalResponse recommendDoctors(DoctorRecommendationRequest request) {
        AiPredictionResponse aiResponse = null;

        List<String> targetSpecializations = new ArrayList<>();

        if (request.getSpecialization() != null) {
            targetSpecializations.add(request.getSpecialization());
        } else {
            aiResponse = aiRecommendationClient.predictSpecialization(request.getSymptoms());

            if (aiResponse.getConfidence() != null
                    && aiResponse.getConfidence() < lowConfidenceLimit
                    && aiResponse.getTopSpecializations() != null
                    && !aiResponse.getTopSpecializations().isEmpty()) {

                targetSpecializations.addAll(aiResponse.getTopSpecializations());
            } else {
                targetSpecializations.add(aiResponse.getPredictedSpecialization());
            }
        }

        List<String> normalizedSpecializations = normalizeSpecializations(targetSpecializations);

        List<DoctorEntity> doctors = doctorRepository.findActiveDoctorsBySpecializations(
                normalizedSpecializations,
                UserStatus.ACTIVE
        );

        List<String> doctorIds = doctors.stream()
                .map(DoctorEntity::getId)
                .toList();

        List<DoctorScheduleEntity> availableSlots = doctorScheduleRepository.findAvailableSlotsForDoctorsByAvailableTrue(doctorIds);

        Map<String, List<DoctorScheduleEntity>> availableSlotsMap = availableSlots.stream()
                .collect(Collectors.groupingBy(DoctorScheduleEntity::getDoctorId));


        List<DoctorResponse> doctorResponses = doctors.stream()
                .map(doctor -> {

                    List<DoctorScheduleEntity> slotEntity = availableSlotsMap.get(doctor.getId());

                    List<DoctorScheduleResponse> doctorScheduleResponses = slotEntity.stream()
                            .map(schedule -> {

                                DoctorScheduleResponse doctorScheduleResponse = modelMapper.map(schedule, DoctorScheduleResponse.class);
                                doctorScheduleResponse.setStartTime(schedule.getStartTime());
                                doctorScheduleResponse.setEndTime(schedule.getEndTime());
                                return doctorScheduleResponse;

                            }).toList();

                    double ratingScore = calculateRatingScore(
                            doctor.getRating(),
                            doctor.getRatingCount()
                    );

                    double experienceScore = calculateExperienceScore(
                            doctor.getExperience()
                    );

                    double specializationScore = calculateSpecializationScore(
                            doctor,
                            normalizedSpecializations
                    );

//                    using Weighted Scoring Algorithm / MCDM.

                    //combining multiple criteria:Rating,Experience,Specialization match
//                   Each one has a weight:Rating = 35%,Experience = 25%,Specialization = 25%

//                  ratingScore contributes 35% ,experienceScore contributes 25% .specializationScore contributes 25%
                    double finalScore = ((0.35 * ratingScore) + (0.25 * experienceScore) + (0.25 * specializationScore)) * 10;


                    DoctorResponse doctorResponse = modelMapper.map(doctor, DoctorResponse.class);
                    doctorResponse.setDoctorId(doctor.getId());
                    if (doctor.getUser() != null) {
                        UserResponse userResponse = modelMapper.map(doctor.getUser(), UserResponse.class);
                        if (doctor.getUser().getRole() != null) {
                            RoleResponse roleResponse = modelMapper.map(doctor.getUser().getRole(), RoleResponse.class);
                            userResponse.setRole(roleResponse.getName());
                        }
                        doctorResponse.setUser(userResponse);
                    }
                    doctorResponse.setSchedules(doctorScheduleResponses);
                    doctorResponse.setRatingScore(roundTwoDecimal(ratingScore));
                    doctorResponse.setExperienceScore(roundTwoDecimal(experienceScore));
                    doctorResponse.setSpecializationScore(roundTwoDecimal(specializationScore));
                    doctorResponse.setFinalScore(roundTwoDecimal(finalScore));

                    return doctorResponse;

                }) .sorted(Comparator.comparing(DoctorResponse::getFinalScore).reversed())
                .toList();

        return GlobalResponseBuilder.buildSuccessResponseWithData("All doctor fetched successfully", doctorResponses);
    }

    private List<String> normalizeSpecializations(List<String> specializations) {

        return specializations.stream()
                .filter(this::hasText)
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private double calculateRatingScore(double rating, int ratingCount) {

        if (ratingCount <= 0) {
            return 3.5 / 5.0;
        }

        return Math.min(rating / 5.0, 1.0);
    }

    private double calculateExperienceScore(double experience) {

        if (experience <= 0) {
            return 0;
        }
//        15 is used as a maximum benchmark experience.
//        15 years or more experience = full experience score
        return Math.min(experience / 15.0, 1.0);
    }

    private double calculateSpecializationScore(
            DoctorEntity doctor,
            List<String> normalizedSpecializations
    ) {

        if (doctor.getSpecialization() == null) {
            return 0;
        }

        String doctorSpecialization = doctor.getSpecialization()
                .trim()
                .toLowerCase();


//        AI predicted = cardiologist
//          Doctor specialization = cardiologist
//        specializationScore = 1.0

        return normalizedSpecializations.contains(doctorSpecialization) ? 1.0 : 0.0;
    }

    private double roundTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
