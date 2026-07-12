package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import com.example.docmate.payload.request.DoctorSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface DoctorService {
    GlobalResponse createDoctor(DoctorRequest doctor,
                                MultipartFile citizenshipFront,MultipartFile citizenshipBack,
                                MultipartFile license, MultipartFile educationCertificate);

    GlobalResponse getAllDoctor(Pageable pageable);

    GlobalResponse getDoctorById(String id);

    GlobalResponse updateDoctor(DoctorRequest doctorRequest, String doctorId,
                                MultipartFile citizenshipFront,MultipartFile citizenshipBack,
                                MultipartFile license, MultipartFile educationCertificate);

    GlobalResponse createDoctorSchedule(DoctorScheduleRequest scheduleRequest);

    GlobalResponse getAllSchedule(String doctorId);

    GlobalResponse getAvailableSlots(String doctorId);

    GlobalResponse getDoctorDetails(String doctorId);

    GlobalResponse deleteSchedule(String doctorId);

    GlobalResponse deleteDoctor(String doctorId);

    GlobalResponse searchDoctor(DoctorSearchRequest doctorRequest,Pageable pageable);

    GlobalResponse ApplyForDoctor(DoctorRequest doctor,
                                  MultipartFile citizenshipFront,MultipartFile citizenshipBack,
                                  MultipartFile license, MultipartFile educationCertificate);
}
