package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import org.springframework.data.domain.Pageable;


public interface DoctorService {
    GlobalResponse createDoctor(DoctorRequest doctor);

    GlobalResponse getAllDoctor(Pageable pageable);

    GlobalResponse getDoctorById(String id);

    GlobalResponse updateDoctor(DoctorRequest doctorRequest, String doctorId);

    GlobalResponse createDoctorSchedule(DoctorScheduleRequest scheduleRequest);

    GlobalResponse getAllSchedule(String doctorId);

    GlobalResponse getAvailableSlots(String doctorId);

    GlobalResponse getDoctorDetails(String doctorId);
}
