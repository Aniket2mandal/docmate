package com.example.docmate.service;

import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.UserRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface DoctorService {
    GlobalResponse createDoctor(DoctorRequest doctor);
    GlobalResponse getAllDoctor();
    GlobalResponse getDoctorById(String id);
    GlobalResponse changeStatus(UserRequest user, String id);
}
