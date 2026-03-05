package com.example.docmate.service;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;


public interface DoctorService {
    GlobalResponse createDoctor(DoctorRequest doctor);
    GlobalResponse getAllDoctor();
}
